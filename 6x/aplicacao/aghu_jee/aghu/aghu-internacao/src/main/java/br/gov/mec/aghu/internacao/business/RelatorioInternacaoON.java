package br.gov.mec.aghu.internacao.business;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFatTipoCaractItem;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.dao.AinMovimentoInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinResponsaveisPacienteDAO;
import br.gov.mec.aghu.internacao.responsaveispaciente.business.IResponsaveisPacienteFacade;
import br.gov.mec.aghu.internacao.vo.RelatorioBoletimInternacaoVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghResponsavel;
import br.gov.mec.aghu.model.AinCidsInternacao;
import br.gov.mec.aghu.model.AinDiariasAutorizadas;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinMovimentosInternacao;
import br.gov.mec.aghu.model.AinResponsaveisPaciente;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class RelatorioInternacaoON extends BaseBusiness {


@EJB
private RelatorioInternacaoRN relatorioInternacaoRN;

private static final Log LOG = LogFactory.getLog(RelatorioInternacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IResponsaveisPacienteFacade responsaveisPacienteFacade;

@EJB
private IFaturamentoFacade faturamentoFacade;

@EJB
private IParametroFacade parametroFacade;

@EJB
private IAghuFacade aghuFacade;

@Inject
private AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO;

@Inject
private AinResponsaveisPacienteDAO ainResponsaveisPacienteDAO;

@EJB
private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7979347321852877660L;

	private enum RelatorioInternacaoONExceptionCode implements
			BusinessExceptionCode {
		AIN00893, AIN00806
	}

	/**
	 * Método para chamada da impressão do Boletim de Internação. Este método é
	 * a implementação do procedure AINP_CHAMA_BOLETIM do form
	 * AINF_INTERNAR_PAC.
	 * 
	 * @param codigo
	 *            da internação
	 * @return RelatorioBoletimInternacaoVO com todos campos setados de acordo
	 *         com busca para geração do relatório
	 */
	public RelatorioBoletimInternacaoVO imprimirRelatorioBoletimInternacao(
			Integer seqInternacao) throws ApplicationBusinessException {

		AinInternacao internacao = getRelatorioInternacaoRN()
				.obterInternacao(seqInternacao);
		
		RelatorioBoletimInternacaoVO relatorio = null;

		if (internacao != null) {
			
			// Feito busca manual da lista de cids da internação, pois no
			// mapeamento foi usado uma anotação de cascade.MERGE que não traz
			// mais a lista automaticamente
			List<AinCidsInternacao> cidsInternacao = getAghuFacade()
					.pesquisarCidsInternacao(seqInternacao);
			if (cidsInternacao != null) {
				Set<AinCidsInternacao> cidsInternacaoSet = new HashSet<AinCidsInternacao>(
						cidsInternacao);
				internacao.setCidsInternacao(cidsInternacaoSet);
			}
			
			// Busca dados do responsável, pois o mesmo pode ter sido incluído na
			// tela de responsáveis da Internação e ainda não salvo no BD, assim não
			// pode aparecer no relatório (pesquisa com projections para furar cache
			// do hibernate)
			/* AinResponsaveisPaciente responsavel = getRelatorioInternacaoRN()
					.obterResponsavelPaciente(seqInternacao);
			List<AinResponsaveisPaciente> responsaveis = new ArrayList<AinResponsaveisPaciente>();
			responsaveis.add(responsavel);*/
			//internacao.setResponsaveisPaciente(responsaveis);
			
			relatorio = this
					.atribuirValoresRelatorioBoletimInternacaoVO(internacao);
		}

		return relatorio;
	}

	/**
	 * Método para verificar se CID exige informar diária autorizada. Esse
	 * método é a implementação da procedure AINP_VALIDA_CID_CERIH do form
	 * AINF_INTERNAR_PAC.
	 * 
	 * @param seq
	 *            da internacao
	 * @param codigo
	 *            do convenio
	 * @throws ApplicationBusinessException
	 */
	public void verificarNecessidadeInformarDiaria(Integer seqInternacao,
			Short codigoConvenio) throws ApplicationBusinessException {

		AinInternacao internacao = getRelatorioInternacaoRN()
				.obterInternacao(seqInternacao);

		Boolean convenioSus = verificarConvenioSus(codigoConvenio);

		if (convenioSus) {
			Boolean caracteristicaCorreta = getFaturamentoFacade().verificarCaracteristicaExame(internacao.getIphSeq(),
																								internacao.getIphPhoSeq(),
																								DominioFatTipoCaractItem.EXIGE_CERIH_INTERNACAO);
			if (!caracteristicaCorreta) {
				return;
			}
		} else {
			return;
		}

		List<AinDiariasAutorizadas> diariaAutorizadaList = internacao.getDiariasAutorizadas();

		if (diariaAutorizadaList == null || diariaAutorizadaList.size() == 0 || diariaAutorizadaList.isEmpty()) {
			throw new ApplicationBusinessException(RelatorioInternacaoONExceptionCode.AIN00893);
		} else {
			if (diariaAutorizadaList.get(0).getSenha() == null && "".equals(diariaAutorizadaList.get(0).getSenha())) {
				throw new ApplicationBusinessException(RelatorioInternacaoONExceptionCode.AIN00893);
			}
		}
	}

	/**
	 * Método para verificar se o convenio é um convenio SUS. Esse método é a
	 * implementação da procedura AINC_CONVENIO_SUS do form AINF_INTERNAR_PAC.
	 * 
	 * @param codigo
	 *            do convenio
	 * @return true/false
	 */
	public Boolean verificarConvenioSus(Short codigoConvenio) {

		List<FatConvenioSaude> convenioList = pesquisarConveniosSaude(codigoConvenio);

		return convenioList.size() == 0 ? false : true;
	}

	private List<FatConvenioSaude> pesquisarConveniosSaude(Short codigoConvenio) {
		return getFaturamentoFacade().pesquisarConveniosSaudeGrupoSUS(codigoConvenio);
	}

	private RelatorioBoletimInternacaoVO atribuirValoresRelatorioBoletimInternacaoVO(
			AinInternacao internacao) throws ApplicationBusinessException {

		RelatorioBoletimInternacaoVO relatorio = new RelatorioBoletimInternacaoVO();

		// Paciente
		AipPacientes paciente = internacao.getPaciente();
		if (paciente != null) {
			relatorio.setIdentificacaoPaciente(paciente);
			String telefone = this.getRelatorioInternacaoRN().formatarTelefone(
					ObjectUtils.toString(paciente.getDddFoneResidencial()),
					ObjectUtils.toString(paciente.getFoneResidencial()),
					ObjectUtils.toString(paciente.getDddFoneRecado()),
					ObjectUtils.toString(paciente.getFoneRecado()));
			relatorio.setPacTelefone(telefone);

			final String labelPosto = "Posto: ";
			String posto = this.getRelatorioInternacaoRN()
					.obterPostoSaudePaciente(paciente.getCodigo());
			if ("".equals(posto) || posto == null) {
				relatorio.setPacPosto("");
			} else {
				relatorio.setPacPosto(labelPosto + ObjectUtils.toString(posto));
			}
		}

		// Responsável
		AinResponsaveisPaciente responsavel = this.ainResponsaveisPacienteDAO.obterResponsaveisPacienteTipoConta(internacao.getSeq());
  		if(responsavel!=null) {
  			if(responsavel.getResponsavelConta()!=null) {
  				AghResponsavel aghResp = this.cadastrosApoioExamesFacade.obterResponsavelPorSeq(responsavel.getResponsavelConta().getSeq());
  				relatorio.setIdentificacaoResponsavelPaciente(null,aghResp);
  			}
  			else {
  				relatorio.setIdentificacaoResponsavelPaciente(responsavel,null);
  			}
  		}

		// Internação
		AinMovimentosInternacao movimentoInternacao = this
				.obterMovimentoInternacao(internacao.getSeq());
		FatItensProcedHospitalar procedimentoHospitalar = this
				.obterItemProcedimentoHospitalar(internacao.getIphSeq(),
						internacao.getIphPhoSeq());
		relatorio.setRelatorioInternacao(internacao, movimentoInternacao,
				procedimentoHospitalar);

		// Equipe Médica
		relatorio.setEquipeMedica(internacao);
		// Labels da equipe médica		
				String labelEquipe = "Equipe:";
				String labelChefeEquipe = "Chefe de Equipe:";
				try {
					if ( getParametroFacade().verificarExisteAghParametro(AghuParametrosEnum.P_LB_BOLETIM_INTERNACAO_EQUIPE) ) {
						AghParametros pLabelEquipe = getParametroFacade()
								.buscarAghParametro(AghuParametrosEnum.P_LB_BOLETIM_INTERNACAO_EQUIPE);
						
						if ( pLabelEquipe != null && !pLabelEquipe.getVlrTexto().isEmpty() ) {
							labelEquipe = pLabelEquipe.getVlrTexto();
						}
					}
					
					if ( getParametroFacade().verificarExisteAghParametro(AghuParametrosEnum.P_LB_BOL_INTERNACAO_CHEFE_EQUIPE) ) {
						AghParametros pLabelChefeEquipe = getParametroFacade()
								.buscarAghParametro(AghuParametrosEnum.P_LB_BOL_INTERNACAO_CHEFE_EQUIPE);		
					
						if ( pLabelChefeEquipe != null && !pLabelChefeEquipe.getVlrTexto().isEmpty() ) {
							labelChefeEquipe = pLabelChefeEquipe.getVlrTexto();
						}
					}
				}
				catch(ApplicationBusinessException e) {
					LOG.error("Exceção AGHUNegocioException capturada, lançada para cima.");
					throw new ApplicationBusinessException(
							RelatorioInternacaoONExceptionCode.AIN00806);
				}
				
				relatorio.setLbEquipe( labelEquipe );
				relatorio.setLbChefeEquipe( labelChefeEquipe );

		// Funcionário que Efetuou Internação
		relatorio.setFuncionarioInternacao(movimentoInternacao);

		// Observação
		relatorio.setObservacao(internacao);

		return relatorio;
	}

	/**
	 * Método para retornar todos movimentos de internação de uma internação.
	 * 
	 * @param seq
	 *            da Internacao
	 * @return Objeto do tipo AinMovimentosInternacao
	 * @throws ApplicationBusinessException
	 */
	private AinMovimentosInternacao obterMovimentoInternacao(
			Integer seqInternacao) throws ApplicationBusinessException {
		try {
			Integer idTipoMovimentacao = recuperaValorParametro(AghuParametrosEnum.P_COD_MVTO_INT_TRSF_UNIDADE);
			
			AinMovimentosInternacao resultMovimentacao = this.getAinMovimentoInternacaoDAO().obterMovimentoInternacaoPorSeqTipo(seqInternacao, idTipoMovimentacao);
			
			if(resultMovimentacao == null){
				Integer idTipoMovimentacaoInternacao = recuperaValorParametro(AghuParametrosEnum.P_COD_MVTO_INT_INTERNACAO);
				resultMovimentacao = this.getAinMovimentoInternacaoDAO().obterMovimentoInternacaoPorSeqTipo(seqInternacao, idTipoMovimentacaoInternacao);
			}
			
			return resultMovimentacao;

		} catch (ApplicationBusinessException e) {
			logError("Exceção ApplicationBusinessException capturada, lançada para cima.");
			throw new ApplicationBusinessException(
					RelatorioInternacaoONExceptionCode.AIN00806);
		}
	}
	
	/**
	 * Método para retornar o id do tipo de movimentação da internação.
	 * A busca é feita através do nome do parametro.
	 * 
	 * @param String
	 *            nome
	 * @return
	 */
	private int recuperaValorParametro(AghuParametrosEnum nomeParametro) throws ApplicationBusinessException {
		AghParametros parametro = getParametroFacade().buscarAghParametro(nomeParametro);
		
		Integer idTipoMovimentacao = parametro.getVlrNumerico() == null ? null
				: parametro.getVlrNumerico().intValue();
		
		return idTipoMovimentacao;
		
	}
	
	

	/**
	 * Método para retornar o item do procedimento hospitalar de um internação.
	 * A busca é feita através do seq e phoSeq.
	 * 
	 * @param Integer
	 *            iphSeq
	 * @param Short
	 *            iphPhoSeq
	 * @return
	 */
	private FatItensProcedHospitalar obterItemProcedimentoHospitalar(
			Integer iphSeq, Short iphPhoSeq) {
		if (iphSeq == null || iphPhoSeq == null) {
			return null;
		} else {
			return getFaturamentoFacade().obterItemProcedimentoHospitalar(iphSeq, iphPhoSeq);
		}
	}
	protected RelatorioInternacaoRN getRelatorioInternacaoRN() {
		return relatorioInternacaoRN;
	}
	
	protected AinMovimentoInternacaoDAO getAinMovimentoInternacaoDAO() {
		return ainMovimentoInternacaoDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IResponsaveisPacienteFacade getResponsaveisPacienteFacade() {
		return responsaveisPacienteFacade;
	}
}