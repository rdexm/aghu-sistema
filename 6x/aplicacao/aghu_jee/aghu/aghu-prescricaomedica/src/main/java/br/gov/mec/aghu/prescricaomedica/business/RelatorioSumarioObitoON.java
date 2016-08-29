package br.gov.mec.aghu.prescricaomedica.business;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoImpressaoRelatorio;
import br.gov.mec.aghu.model.FatSituacaoSaidaPaciente;
import br.gov.mec.aghu.model.MpmAltaDiagPrincipal;
import br.gov.mec.aghu.model.MpmAltaEstadoPaciente;
import br.gov.mec.aghu.model.MpmAltaOtrProcedimento;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmObitoNecropsia;
import br.gov.mec.aghu.model.MpmObtCausaAntecedente;
import br.gov.mec.aghu.model.MpmObtCausaDireta;
import br.gov.mec.aghu.model.MpmObtGravidezAnterior;
import br.gov.mec.aghu.model.MpmObtOutraCausa;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaCirgRealizadaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaComplFarmacoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaDiagMtvoInternacaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaDiagPrincipalDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaDiagSecundarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaEvolucaoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaOtrProcedimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaOutraEquipeSumrDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaPrincFarmacoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObitoNecropsiaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObtCausaAntecedenteDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObtCausaDiretaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObtGravidezAnteriorDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmObtOutraCausaDAO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioSumarioObitoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class RelatorioSumarioObitoON extends BaseBusiness {

	@EJB
	private LaudoProcedimentoSusRN laudoProcedimentoSusRN;
	
	private static final Log LOG = LogFactory.getLog(RelatorioSumarioObitoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private MpmAltaComplFarmacoDAO mpmAltaComplFarmacoDAO;
	
	@Inject
	private MpmAltaSumarioDAO mpmAltaSumarioDAO;
	
	@Inject
	private MpmAltaConsultoriaDAO mpmAltaConsultoriaDAO;
	
	@Inject
	private MpmAltaPrincFarmacoDAO mpmAltaPrincFarmacoDAO;
	
	@Inject
	private MpmObtOutraCausaDAO mpmObtOutraCausaDAO;
	
	@Inject
	private MpmAltaDiagMtvoInternacaoDAO mpmAltaDiagMtvoInternacaoDAO;
	
	@Inject
	private MpmObtGravidezAnteriorDAO mpmObtGravidezAnteriorDAO;
	
	@Inject
	private MpmAltaEvolucaoDAO mpmAltaEvolucaoDAO;
	
	@Inject
	private MpmAltaDiagPrincipalDAO mpmAltaDiagPrincipalDAO;
	
	@Inject
	private MpmAltaOtrProcedimentoDAO mpmAltaOtrProcedimentoDAO;
	
	@Inject
	private MpmObtCausaAntecedenteDAO mpmObtCausaAntecedenteDAO;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private MpmAltaDiagSecundarioDAO mpmAltaDiagSecundarioDAO;
	
	@Inject
	private MpmAltaCirgRealizadaDAO mpmAltaCirgRealizadaDAO;
	
	@Inject
	private MpmAltaOutraEquipeSumrDAO mpmAltaOutraEquipeSumrDAO;
	
	@Inject
	private MpmObitoNecropsiaDAO mpmObitoNecropsiaDAO;
	
	@Inject
	private MpmObtCausaDiretaDAO mpmObtCausaDiretaDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8276582495235081618L;

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public RelatorioSumarioObitoVO criaRelatorioSumarioAlta(MpmAltaSumarioId id, String  tipoImpressao) throws BaseException{
		
		MpmAltaSumario altaSumario = this.getMpmAltaSumarioDAO().obterAltaSumarioPeloId(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		
		RelatorioSumarioObitoVO relSumario = new RelatorioSumarioObitoVO();
		
		relSumario.setpAsuAtdSeq(altaSumario.getId().getApaAtdSeq());
		relSumario.setpAsuApaSeq(altaSumario.getId().getApaSeq());
		relSumario.setpAsuSeq(altaSumario.getId().getSeqp().intValue());
		
		relSumario.setTipoImpressao(DominioTipoImpressaoRelatorio.valueOf(tipoImpressao));
		
		//identificação
		relSumario.setNomePaciente(altaSumario.getNome());
		relSumario.setIdade(criaIdadeExtenso(altaSumario.getIdadeAnos(),altaSumario.getIdadeMeses(),altaSumario.getIdadeDias()));
		relSumario.setSexo(altaSumario.getSexo().getDescricao());
		relSumario.setDataInternacao(altaSumario.getDthrAtendimento());
		relSumario.setDataObito(altaSumario.getDthrAlta());
		relSumario.setProntuario(CoreUtil.formataProntuarioRelatorio(altaSumario.getProntuario()));
		
		if (altaSumario.getLeito() != null) {
			relSumario.setLeito(altaSumario.getLeito().getLeitoID());	
		} else {
			relSumario.setLeito(" ");
		}
		
		relSumario.setPermanencia(altaSumario.getDiasPermanencia() + " dias");	
		relSumario.setConvenio(altaSumario.getDescConvenio());
		relSumario.setEquipeResponsavel(altaSumario.getDescEquipeResponsavel());
		relSumario.setServicoResponsavel(altaSumario.getDescServicoResponsavel());	
		relSumario.setIndetificador(id.toString());

		Integer matriculaValida = altaSumario.getServidorValida() != null ? altaSumario.getServidorValida().getId().getMatricula() : null;
		Short 	vinCodigoValida = altaSumario.getServidorValida() != null ? altaSumario.getServidorValida().getId().getVinCodigo() : null;
		relSumario.setNomeSiglaRegistro(buscaNomeSiglaRegistro(matriculaValida, vinCodigoValida));
		
		if(altaSumario.getDthrElaboracaoAlta() != null){
			relSumario.setDthrElaboracaoObito(altaSumario.getDthrElaboracaoAlta());
		}else{
			relSumario.setDthrElaboracaoObito(new Date());
		}

		relSumario.setOutrasEquipes(buscaOutrasEquipes(id));

		Integer indice = 1;
		//diagnósticos
		relSumario.setMotivosInternacao(buscaAltaDiagMtvoInternacoes(id));
		relSumario.setDiagnosticoPrincipalObito(buscaAltaDiagPrincipal(id));
        relSumario.setDiagnosticosSecundarios(buscaAltaDiagSecundarios(id));
        
		
		if ((relSumario.getMotivosInternacao() != null && !relSumario.getMotivosInternacao().isEmpty()) 
		  	 || (relSumario.getDiagnosticoPrincipalObito() != null && !relSumario.getDiagnosticoPrincipalObito().isEmpty())
		  	 || (relSumario.getDiagnosticosSecundarios() !=null && !relSumario.getDiagnosticosSecundarios().isEmpty())) {
			
			relSumario.setIndiceDiagnostico(++indice + ".");
			
		}

        
        //procedimentos terapêuticos
		relSumario.setCirurgiasRealizadas(buscaAltaCirgRealizadas(id));
		relSumario.setOutrosProcedimentos(buscaOutrosProcedimentos(id));
		relSumario.setConsultorias(buscaAltaConsultorias(id));
		relSumario.setPrincipaisFarmacos(buscaPrincFarmacos(id));   
		relSumario.setComplementoFarmacos(buscaComplFarmacos(id));

		if ((relSumario.getCirurgiasRealizadas()!=null && !relSumario.getCirurgiasRealizadas().isEmpty())
				||(relSumario.getOutrosProcedimentos()!=null && !relSumario.getOutrosProcedimentos().isEmpty())
				||(relSumario.getConsultorias()!=null && !relSumario.getConsultorias().isEmpty())
				||(relSumario.getPrincipaisFarmacos()!=null && !relSumario.getPrincipaisFarmacos().isEmpty())
				||(relSumario.getComplementoFarmacos()!=null && !relSumario.getComplementoFarmacos().isEmpty())){
				
				relSumario.setIndiceProcedTerapeutico(++indice + ".");
			}
		
		
		//evolução
		relSumario.setEvolucao(buscaEvolucoes(id));
		
		if (relSumario.getEvolucao() != null){
			relSumario.setIndiceEvolucao(++indice + ".");
		}
		

		//informaçoes de óbito
		MpmObtCausaDireta causas = buscaCausaDireta(id);
		if (causas != null && causas.getDescCid() != null) {
			relSumario.setObtCausaDiretas(causas.getDescCid() );
		}
		if (causas != null && causas.getComplCid() !=null ) {
			relSumario.setObtCausaDiretas(relSumario.getObtCausaDiretas());
		}
		
 		relSumario.setObtCausaAntecedentes(buscarCausaAntecedente(id));
 		relSumario.setObtOutrasCausas(buscarOutraCausa(id));
		relSumario.setObtNecropsias(buscarSolicitacaoNecropsia(id));
		relSumario.setObtGravidezAnteriores(buscarGravidezAnterior(id));

		if ((relSumario.getObtCausaDiretas()!=null && !relSumario.getObtCausaDiretas().isEmpty())
				||(relSumario.getObtCausaAntecedentes()!=null && !relSumario.getObtCausaAntecedentes().isEmpty())
				||(relSumario.getObtOutrasCausas()!=null && !relSumario.getObtOutrasCausas().isEmpty())
				||(relSumario.getObtNecropsias()!=null && !relSumario.getObtNecropsias().isEmpty())
				||(relSumario.getObtGravidezAnteriores()!=null && !relSumario.getObtGravidezAnteriores().isEmpty())){
				
				relSumario.setIndiceInfObito(++indice + ".");
			}

		
		
		
		//identificação rodape
		
		// paciente recém nacido requer prontuário da mãe no relatório
		relSumario.setProntuarioRodape(CoreUtil.formataProntuarioRelatorio(altaSumario.getProntuario()));
		relSumario.setNomeRodape(altaSumario.getNome());
		if (altaSumario.getProntuario() != null
				&& altaSumario.getProntuario() > VALOR_MAXIMO_PRONTUARIO) {

			if (altaSumario.getPaciente().getMaePaciente() != null) {
				relSumario.setProntuarioRodape(CoreUtil
						.formataProntuarioRelatorio(altaSumario
								.getPaciente().getProntuario())
						+ "          Mãe: "
						+ CoreUtil.formataProntuarioRelatorio(altaSumario.getPaciente()
								.getMaePaciente().getProntuario()));
			}
		}
        // localização Leito ou Quarto 
//		if  ((altaSumario.getLeito() != null)&&(altaSumario.getLeito().getLeitoID() != null))  {
//			relSumario.setLocalRodape("Leito: "+ altaSumario.getLeito().getLeitoID());
//		} else if ((altaSumario.getQuarto() != null)&&(altaSumario.getQuarto().getNumero() != null)) {
//			relSumario.setLocalRodape("Quarto: "+ altaSumario.getQuarto().getNumero());
//		}
				
		if (altaSumario.getLeito() != null) {
			relSumario.setLocalRodape("Leito: " + altaSumario.getLeito().getLeitoID());
		} else if (altaSumario.getQuarto() != null) {
			relSumario.setLocalRodape("Quarto: " + altaSumario.getQuarto().getDescricao());
		} else if (altaSumario.getUnidadeFuncional() != null) {
			StringBuffer unidade = null;
			if (altaSumario.getUnidadeFuncional().getSigla() != null) {
				unidade = new StringBuffer(altaSumario.getUnidadeFuncional().getSigla());
			} else {
				unidade = new StringBuffer(altaSumario.getUnidadeFuncional().getAndar().toString());
				if (altaSumario.getUnidadeFuncional().getIndAla() != null) {
					unidade.append(altaSumario.getUnidadeFuncional().getIndAla());
				}
			}
			relSumario.setLocalRodape(unidade.insert(0, "Unidade: ").toString());
		}
		
		// #46268 – Óbito e sumário de Óbito – situação de saída do paciente
		relSumario.setIndiceSituacaoSaidaPaciente(++indice + ".");
		final MpmAltaEstadoPaciente estadoPaciente = this.prescricaoMedicaFacade.obterMpmAltaEstadoPacientePorChavePrimaria(altaSumario.getId());
		
		if (estadoPaciente != null) {
			final FatSituacaoSaidaPaciente situacaoSaidaPaciente = estadoPaciente.getEstadoPaciente();
			if (situacaoSaidaPaciente != null && situacaoSaidaPaciente.getMotivoSaidaPaciente() != null) {
				relSumario.setSituacaoSaidaPaciente(situacaoSaidaPaciente.getDescricaoEditada()+" ("+estadoPaciente.getEstadoPaciente().getMotivoSaidaPaciente().getCodigoSus()+estadoPaciente.getEstadoPaciente().getCodigoSus()+")");
			}
		}
		return relSumario;
	}
	
	
	private String buscaNomeSiglaRegistro(Integer matricula, Short vinculo) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogadoSemCache();
		
		BuscaConselhoProfissionalServidorVO vo = laudoProcedimentoSusRN.buscaConselhoProfissionalServidorVO(matricula, vinculo, Boolean.FALSE);
		if(StringUtils.isNotBlank(vo.getSiglaConselho()) && StringUtils.isNotBlank(vo.getNome())){
			return substituiNullPorEspaco(vo.getNome(),true) + 
				   substituiNullPorEspaco(vo.getSiglaConselho(),true)+ 
				   substituiNullPorEspaco(vo.getNumeroRegistroConselho(),false);
		}else{
			if(matricula != null && vinculo != null){
				return getRegistroColaboradorFacade().buscarServidor(vinculo, matricula).getPessoaFisica().getNome();
			}else{
				return servidorLogado.getPessoaFisica().getNome();
			}
		}
	}	
	
	private List<LinhaReportVO> buscaOutrasEquipes(final MpmAltaSumarioId id){
		return this.getMpmAltaOutraEquipeSumrDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
	}
	
	private List<LinhaReportVO> buscaAltaDiagMtvoInternacoes(final MpmAltaSumarioId id){
		return this.getMpmAltaDiagMtvoInternacaoDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
	}
	
	private String buscaAltaDiagPrincipal(final MpmAltaSumarioId id) throws ApplicationBusinessException{
		MpmAltaDiagPrincipal altaDiagPrincipal = this.getMpmAltaDiagPrincipalDAO().obterAltaDiagPrincipal(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		if (altaDiagPrincipal != null) {
			
        StringBuffer texto = new StringBuffer(altaDiagPrincipal.getDescCid());
        //concatena o CID

        //if (altaDiagPrincipal.getCid().getCodigo() !=null) {
       //     texto.append(" ("+ altaDiagPrincipal.getCid().getCodigo()+") "  );
       // }
        
        if (altaDiagPrincipal.getComplCid() != null) {
        	texto.append(' ').append(altaDiagPrincipal.getComplCid());	
        }
        return texto.toString();
        
		} else {
			return null;
		}        
		
	}	

	private List<LinhaReportVO> buscaAltaDiagSecundarios(final MpmAltaSumarioId id){
		return this.getMpmAltaDiagSecundarioDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
	}	
	
	private List<LinhaReportVO> buscaAltaCirgRealizadas(final MpmAltaSumarioId id){
		return this.getMpmAltaCirgRealizadaDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
	}		

	private List<LinhaReportVO> buscaOutrosProcedimentos(final MpmAltaSumarioId id) throws ApplicationBusinessException{
		//return this.getMpmAltaOtrProcedimentoDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		List<LinhaReportVO> listaLinhaReportVO = new ArrayList<LinhaReportVO>(); 
		
		List<MpmAltaOtrProcedimento> otrProcedimento = this.getMpmAltaOtrProcedimentoDAO().obterMpmAltaOtrProcedimento(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		
		
		for (MpmAltaOtrProcedimento altaOtrProcedimento : otrProcedimento) {
			// concatena Procedimento e Outros Procedimentos
			LinhaReportVO linhaReportVO = new LinhaReportVO();
			linhaReportVO.setData(altaOtrProcedimento.getDthrOutProcedimento());
			
			if (altaOtrProcedimento.getComplOutProcedimento() != null) {
				linhaReportVO.setTexto1(altaOtrProcedimento
						.getComplOutProcedimento());
			} else if (altaOtrProcedimento.getDescOutProcedimento() != null) {
				linhaReportVO.setTexto1(altaOtrProcedimento
						.getDescOutProcedimento());
			}
			
			listaLinhaReportVO.add(linhaReportVO);

		}

		return  listaLinhaReportVO;
		
		
	}
	
	private List<LinhaReportVO> buscaAltaConsultorias(final MpmAltaSumarioId id){
		return this.getMpmAltaConsultoriaDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
	}		
	
	private List<LinhaReportVO> buscaPrincFarmacos(final MpmAltaSumarioId id){
		return this.getMpmAltaPrincFarmacoDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
	}		
	
	private List<LinhaReportVO> buscaComplFarmacos(final MpmAltaSumarioId id){
		return this.getMpmAltaComplFarmacoDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
	}		

	private String buscaEvolucoes(final MpmAltaSumarioId id){
		return this.getMpmAltaEvolucaoDAO().obterRelatorioSumario(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
	}		
	
	private MpmObtCausaDireta buscaCausaDireta (final MpmAltaSumarioId id){
		return this.getMpmObtCausaDiretaDAO().obterObtCausaDireta(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
	}
	
	/**
	 * Busca causa antecedente do Òbito para relatório.
	 * @param id
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private List<LinhaReportVO>  buscarCausaAntecedente (final MpmAltaSumarioId id) throws BaseException  {
		
		List<LinhaReportVO> listaLinhaReportVO = new ArrayList<LinhaReportVO>(); 
		
		List<MpmObtCausaAntecedente> causaAntecedente = this.getMpmObtCausaAntecedenteDAO().obterMpmObtCausaAntecedente(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		
		
		for (MpmObtCausaAntecedente obtCausaAntecedente:causaAntecedente) {
            //concatena Desc Cid e Complemento no Texto 1

			LinhaReportVO linhaReportVO = new LinhaReportVO(); 
			if (obtCausaAntecedente.getDescCid() != null) {
				linhaReportVO.setTexto1(obtCausaAntecedente.getDescCid());
			}
			//if (obtCausaAntecedente.getCid().getCodigo() !=null) {
			//	linhaReportVO.setTexto1(linhaReportVO.getTexto1() + " (" +obtCausaAntecedente.getCid().getCodigo()+")");
			//}
			
			if (obtCausaAntecedente.getComplCid() != null) {
				linhaReportVO.setTexto1(linhaReportVO.getTexto1() + " " +obtCausaAntecedente.getComplCid());
			}

			listaLinhaReportVO.add(linhaReportVO);
			
		}

		return  listaLinhaReportVO;
	}

	/**
	 * Busca outras causas do Òbito para relatório.
	 * @param id
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private List<LinhaReportVO>  buscarOutraCausa (final MpmAltaSumarioId id) throws BaseException   {
		
		List<LinhaReportVO> listaLinhaReportVO = new ArrayList<LinhaReportVO>(); 
		
		List<MpmObtOutraCausa> outraCausa = this.getMpmObtOutraCausaDAO().obterMpmObtOutraCausa(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		
		
		for (MpmObtOutraCausa obtOutraCausa:outraCausa) {
            //concatena Desc Cid e Complemento no Texto 1
			
			LinhaReportVO linhaReportVO = new LinhaReportVO(); 
			if (obtOutraCausa.getDescCid() != null) {
				linhaReportVO.setTexto1(obtOutraCausa.getDescCid());
			}

			//if (obtOutraCausa.getCid().getCodigo() !=null) {
			//	linhaReportVO.setTexto1(linhaReportVO.getTexto1() + " (" +obtOutraCausa.getCid().getCodigo()+")");
			//}
			
			if (obtOutraCausa.getComplCid() != null) {
				linhaReportVO.setTexto1(linhaReportVO.getTexto1()+ " " +obtOutraCausa.getComplCid());
			}
			
			listaLinhaReportVO.add(linhaReportVO);
			
		}

		return  listaLinhaReportVO;
	}
	
	/**
	 * Verifica se foi solicitada Necrópsia
	 * @param id
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private String  buscarSolicitacaoNecropsia (final MpmAltaSumarioId id) throws ApplicationBusinessException  {

	MpmObitoNecropsia obitoNecropsia = this.getMpmObitoNecropsiaDAO().obterMpmObitoNecropsia(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
	
    if  ((obitoNecropsia !=null)&&(DominioSimNao.S.equals(obitoNecropsia.getNecropsia()))) {
    	return "Solicitada a realização de Necrópsia";
    }
	
	return null ; 
	}	
	

	private String  buscarGravidezAnterior (final MpmAltaSumarioId id) throws ApplicationBusinessException  {

		MpmObtGravidezAnterior gravidezAnterior = this.getMpmObtGravidezAnteriorDAO().obterMpmObtGravidezAnterior(id.getApaAtdSeq(), id.getApaSeq(), id.getSeqp());
		
		
		
	    if  ((gravidezAnterior != null)&&(DominioSimNao.S.equals(gravidezAnterior.getGravidezAnterior()))) {
	    	return "A paciente esteve grávida nos últimos 12 meses";
	    }
		
		return null ; 
		}	
	
	private String criaIdadeExtenso(Short anos, Integer meses, Integer dias){		
		StringBuffer idadeExt = new StringBuffer();
		if (anos!=null && anos > 1) {
			idadeExt.append(anos).append(" anos ");
		}else if (anos!=null && anos == 1) {
			idadeExt.append(anos).append(" ano ");
		}
		if (meses!=null && meses > 1) {
			idadeExt.append(meses).append(" meses ");
		}else if (meses!=null && meses == 1) {
			idadeExt.append(meses).append(" mês ");
		}
		if (anos==null || anos == 0){ 
			if (dias!=null && dias > 1) {
				idadeExt.append(dias).append(" dias ");
			}else if (dias!=null && dias == 1) {
				idadeExt.append(dias).append(" dia ");
			}
		}		
		return idadeExt.toString();
	}
	
	private String substituiNullPorEspaco(String str, boolean addEspaco){
		if (str==null){
			return "";
		}else{
			if (addEspaco){
				return str+" ";
			}else{
				return str;
			}	
		}
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}
	
	protected MpmAltaCirgRealizadaDAO getMpmAltaCirgRealizadaDAO() {
		return mpmAltaCirgRealizadaDAO;
	}
	
	protected MpmAltaComplFarmacoDAO getMpmAltaComplFarmacoDAO() {
		return mpmAltaComplFarmacoDAO;
	}
	
	protected MpmAltaConsultoriaDAO getMpmAltaConsultoriaDAO() {
		return mpmAltaConsultoriaDAO;
	}
	
	protected MpmAltaDiagMtvoInternacaoDAO getMpmAltaDiagMtvoInternacaoDAO() {
		return mpmAltaDiagMtvoInternacaoDAO;
	}
	
	protected MpmAltaDiagPrincipalDAO getMpmAltaDiagPrincipalDAO() {
		return mpmAltaDiagPrincipalDAO;
	}
	
	protected MpmAltaDiagSecundarioDAO getMpmAltaDiagSecundarioDAO() {
		return mpmAltaDiagSecundarioDAO;
	}
	
	protected MpmAltaEvolucaoDAO getMpmAltaEvolucaoDAO() {
		return mpmAltaEvolucaoDAO;
	}
	
	protected MpmAltaOtrProcedimentoDAO getMpmAltaOtrProcedimentoDAO() {
		return mpmAltaOtrProcedimentoDAO;
	}
	
	protected MpmAltaOutraEquipeSumrDAO getMpmAltaOutraEquipeSumrDAO() {
		return mpmAltaOutraEquipeSumrDAO;
	}
	
	protected MpmAltaPrincFarmacoDAO getMpmAltaPrincFarmacoDAO() {
		return mpmAltaPrincFarmacoDAO;
	}
	
	protected MpmAltaSumarioDAO getMpmAltaSumarioDAO() {
		return mpmAltaSumarioDAO;
	}
	
	protected MpmObitoNecropsiaDAO getMpmObitoNecropsiaDAO() {
		return mpmObitoNecropsiaDAO;
	}
	
	protected MpmObtCausaAntecedenteDAO getMpmObtCausaAntecedenteDAO() {
		return mpmObtCausaAntecedenteDAO;
	}
	
	protected MpmObtCausaDiretaDAO getMpmObtCausaDiretaDAO() {
		return mpmObtCausaDiretaDAO;
	}
	
	protected MpmObtGravidezAnteriorDAO getMpmObtGravidezAnteriorDAO() {
		return mpmObtGravidezAnteriorDAO;
	}
	
	protected MpmObtOutraCausaDAO getMpmObtOutraCausaDAO() {
		return mpmObtOutraCausaDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
