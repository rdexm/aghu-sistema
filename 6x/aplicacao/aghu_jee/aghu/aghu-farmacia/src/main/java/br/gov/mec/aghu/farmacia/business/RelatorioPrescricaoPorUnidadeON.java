package br.gov.mec.aghu.farmacia.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.dao.AfaPrescricaoMedicamentoDAO;
import br.gov.mec.aghu.farmacia.vo.PrescricaoUnidadeVO;
import br.gov.mec.aghu.model.AfaPrescricaoMedicamento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;


@Stateless
public class RelatorioPrescricaoPorUnidadeON extends BaseBusiness implements Serializable{

	
	@EJB
	private RelatorioPrescricaoPorUnidadeRN relatorioPrescricaoPorUnidadeRN;
	
	@Inject
	private AfaPrescricaoMedicamentoDAO afaPrescricaoMedicamentoDAO;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	private static final long serialVersionUID = 3179888625818725376L;
	private static final String OBSERVACAO_RELATORIO_PRESCRICAO = "Obs.: U-Prescrição em Uso   *-Prescrição Pendente   A-Prescrição com Alta Médica";
	private static final Log LOG = LogFactory.getLog(RelatorioPrescricaoPorUnidadeON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	/**
	 * Obtem dados para o Relatório Prescrição por Unidade
	 * 
	 * @param unidadeFuncional
	 * @param dataDeReferencia
	 * @param validade
	 * @param indPmNaoEletronica
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<PrescricaoUnidadeVO> obterConteudoRelatorioPrescricaoPorUnidade(
			AghUnidadesFuncionais unidadeFuncional, Date dataDeReferencia,
			String validade, Boolean indPmNaoEletronica) throws ApplicationBusinessException {

		List<PrescricaoUnidadeVO> listaVOs = new ArrayList<PrescricaoUnidadeVO>();
		List<AghAtendimentos> listaAtendimentos = getAghuFacade().pesquisarAtendimentosaPorUnidadeDataReferencia(unidadeFuncional, dataDeReferencia);
		
		//if(!Boolean.TRUE.equals(indPmNaoEletronica)){//Deve carregar esta lista SE indPmNaoEletronica for NULL ou FALSE
			
			for (AghAtendimentos atendimento: listaAtendimentos){
				Set<MpmPrescricaoMedica> prescricoesEletronicas =  atendimento.getMpmPrescricaoMedicases();
				List<AfaPrescricaoMedicamento> prescricoesNaoEletronicas = getAfaPrescricaoMedicamentoDAO().
																			pesquisarPrescricaoMedicamentosByAtdSeqEDtRefrencia
																				(atendimento.getSeq(), dataDeReferencia);
				if((prescricoesEletronicas == null || prescricoesEletronicas.isEmpty())
						&& (prescricoesNaoEletronicas == null || prescricoesNaoEletronicas.isEmpty())){
					PrescricaoUnidadeVO prescricaoUnidadeVO  = processaPrescricaoUnidadeVO(atendimento,unidadeFuncional,validade, dataDeReferencia, null, null);
					listaVOs.add(prescricaoUnidadeVO);
				}
				
				if(!Boolean.TRUE.equals(indPmNaoEletronica)){//CASO TENHA SELECIONADO NULL OU ELETRONICA
					for(MpmPrescricaoMedica prescricaoMedica:prescricoesEletronicas){
						PrescricaoUnidadeVO prescricaoUnidadeVO = processaPrescricaoUnidadeVO(atendimento,unidadeFuncional,validade, dataDeReferencia, prescricaoMedica, null);
						listaVOs.add(prescricaoUnidadeVO);
					}
				}
				
				if(!Boolean.FALSE.equals(indPmNaoEletronica)){//CASO TENHA SELECIONADO NULL OU 'NÃO ELETRONICA
					for(AfaPrescricaoMedicamento pmm :prescricoesNaoEletronicas){
						PrescricaoUnidadeVO prescricaoUnidadeVO = processaPrescricaoUnidadeVO(atendimento,unidadeFuncional,validade, dataDeReferencia, null, pmm);
						listaVOs.add(prescricaoUnidadeVO);
					}
				}
				
		}
		
		CoreUtil.ordenarLista(listaVOs, "prontuario", true);
		CoreUtil.ordenarLista(listaVOs, "indPmNaoEletronica", true);

		return listaVOs;
	}

	/**
	 * Popula VO para o Relatório Prescrição por Unidade
	 * 
	 * @param atendimento
	 * @param unidadeFuncional
	 * @param validade
	 * @param dataDeReferencia
	 * @param prescricaoMedica
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private PrescricaoUnidadeVO processaPrescricaoUnidadeVO(AghAtendimentos atendimento, AghUnidadesFuncionais unidadeFuncional, String validade, 
			Date dataDeReferencia, MpmPrescricaoMedica prescricaoMedica, AfaPrescricaoMedicamento prescricaoMedicamento) throws ApplicationBusinessException {
		
		PrescricaoUnidadeVO prescricaoUnidadeVO = new PrescricaoUnidadeVO();
		//Formata o campo de Andar/Ala e seta
		StringBuffer andarAlaFormat = new StringBuffer(unidadeFuncional.getAndar().toString()) 
										.append(' ').append(unidadeFuncional.getIndAla()) 
										.append("    ").append(unidadeFuncional.getSeq()) 
										.append('-').append(unidadeFuncional.getDescricao());
		prescricaoUnidadeVO.setAndarAla(andarAlaFormat.toString());

		//Seta a validade
		prescricaoUnidadeVO.setValidade(validade);
		//Seta o conf
		prescricaoUnidadeVO.setConf("[   	]");
		//Seta a Localizacao
		prescricaoUnidadeVO.setLocalizacao(getRelatorioPrescricaoPorUnidadeRN().obterLocalizacaoPacienteParaRelatorio(atendimento));
		//Seta o Prontuário
		prescricaoUnidadeVO.setProntuario(atendimento.getProntuario().toString());
		//Seta a Observação
		prescricaoUnidadeVO.setObsrvacao(OBSERVACAO_RELATORIO_PRESCRICAO);
		
		if(prescricaoMedica != null){
			//Seta a SituacaoConfPresc
			prescricaoUnidadeVO.setSituacaoConfPresc(getRelatorioPrescricaoPorUnidadeRN().obterSituacaoPrescricao(atendimento, dataDeReferencia, prescricaoMedica));
			//Seta a Prescrição
			prescricaoUnidadeVO.setSeqPrescricao(prescricaoMedica.getId().getSeq());
			//Seta a Data/Hora
			String dataHoraPrescricao = DateUtil.obterDataFormatada(prescricaoMedica.getCriadoEm(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO);
			prescricaoUnidadeVO.setDataHora(dataHoraPrescricao);
			prescricaoUnidadeVO.setIndPmNaoEletronica(Boolean.FALSE);
		}else{
			if(prescricaoMedicamento != null){
				prescricaoUnidadeVO.setSituacaoConfPresc(null);
				prescricaoUnidadeVO.setSeqPrescricao(prescricaoMedicamento.getSeq().intValue());
				String dataHoraPrescricao = DateUtil.obterDataFormatada(prescricaoMedicamento.getCriadoEm(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO);
				prescricaoUnidadeVO.setDataHora(dataHoraPrescricao);
				prescricaoUnidadeVO.setIndPmNaoEletronica(Boolean.TRUE);
			}else{
				prescricaoUnidadeVO.setSeqPrescricao(null);
				prescricaoUnidadeVO.setDataHora(null);
				prescricaoUnidadeVO.setIndPmNaoEletronica(null);
			}
		}
		return prescricaoUnidadeVO;
	}

	public String atribuirValidadeDaPrescricaoMedica (Date dataDeReferencia, AghUnidadesFuncionais unidadeFuncional){

		String validade;
		String horario;
		Date dataDeReferencia2;
		
		dataDeReferencia2 = DateUtil.adicionaDias(dataDeReferencia, 1);
			
		horario =  getAghuFacade().pegaHorarioDaUnidadeFuncional(unidadeFuncional);	

		validade = DateFormatUtil.fomataDiaMesAno(dataDeReferencia) + " " + horario + " a  " + 
					DateFormatUtil.fomataDiaMesAno(dataDeReferencia2) + " " + horario;
		
		return validade;
		
	}
	
	/**
	 * Pesquisa Unidades Funcionais somente pelas características necessárias para
	 * o relatório de prescrição por unidade.
	 * @param parametro
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(
			Object parametro) {
		return getAghuFacade()
				.pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicas(
						parametro,
						null,
						Boolean.TRUE,
						Boolean.TRUE,
						Boolean.TRUE,
						Arrays.asList(AghUnidadesFuncionais.Fields.ANDAR
								, AghUnidadesFuncionais.Fields.ALA
								, AghUnidadesFuncionais.Fields.DESCRICAO),
						ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO, 
						ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA,
						ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA, 
						ConstanteAghCaractUnidFuncionais.CO);
	}
	
	/**
	 * Count de Unidades Funcionais somente pelas características necessárias para
	 * o relatório de prescrição por unidade.
	 * @param parametro
	 * @return
	 */
	public Long pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicasCount(
			Object parametro) {
		return getAghuFacade()
		.pesquisarUnidadesFuncionaisPorCodigoDescricaoECaracteristicasCount(
				parametro,
				DominioSituacao.A,
				Boolean.TRUE,
				Boolean.TRUE,
				Boolean.TRUE,
				ConstanteAghCaractUnidFuncionais.UNID_INTERNACAO, 
				ConstanteAghCaractUnidFuncionais.UNID_HOSP_DIA,
				ConstanteAghCaractUnidFuncionais.UNID_EMERGENCIA, 
				ConstanteAghCaractUnidFuncionais.CO,
				ConstanteAghCaractUnidFuncionais.UNID_AMBULATORIO,
				ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA);
	}
	
	public RelatorioPrescricaoPorUnidadeRN getRelatorioPrescricaoPorUnidadeRN(){
		return relatorioPrescricaoPorUnidadeRN;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}
	
	protected AfaPrescricaoMedicamentoDAO getAfaPrescricaoMedicamentoDAO(){
		return afaPrescricaoMedicamentoDAO;
	}
}
