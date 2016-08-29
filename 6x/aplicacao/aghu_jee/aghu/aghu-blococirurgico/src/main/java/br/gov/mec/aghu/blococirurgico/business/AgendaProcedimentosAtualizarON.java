package br.gov.mec.aghu.blococirurgico.business;


import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.AgendaProcedimentosON.AgendaProcedimentosONExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaVO;
import br.gov.mec.aghu.blococirurgico.vo.PesquisaPrincipalTelaVO;
import br.gov.mec.aghu.blococirurgico.vo.PesquisaQuantidadeEquipeResponsavelVO;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Classe responsável pelas regras de FORMS do INSERIR de #22460: Agendar procedimentos eletivo, urgência ou emergência Classe criada para suprir as violações de PMD (tamanho de
 * classe) que ocorriam na classe AgendaProcedimentosON
 * 
 * @author aghu
 * 
 */

@Stateless
public class AgendaProcedimentosAtualizarON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AgendaProcedimentosAtualizarON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	

	@Inject
	private MbcDescricaoCirurgicaDAO mbcDescricaoCirurgicaDAO;


	@EJB
	private ICascaFacade iCascaFacade;

	@EJB
	private VerificaBloqueioCirurgiaRN verificaBloqueioCirurgiaRN;

	@EJB
	private AgendaProcedimentosON agendaProcedimentosON;

	@EJB
	private AtualizarAgendaRN atualizarAgendaRN;

	@EJB
	private AgendaProcedimentosFuncoesON agendaProcedimentosFuncoesON;

	@EJB
	private MbcCirurgiasVerificacoesRN mbcCirurgiasVerificacoesRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4844268189004683316L;

	/**
	 * ORADB PROCEDURE EVT_PRE_UPDATE
	 * <p>
	 * RN34
	 * <p>
	 * 
	 * @param vo
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public String preUpdate(CirurgiaTelaVO vo) throws BaseException {

		String mensagemRetorno = null; // Retorno da mensagem de confirmação na tela

		/*
		 * ATENÇÃO: Foi combinado com o analista que TODAS AS VALIDAÇÕES devem ocorrer independente do bloco chamado. Vide: name_in('system.trigger_block'). As chamadas no AGHU
		 * ocorrem para todos os blocos/fieldsets.
		 */

		// Se o usuário é médico, só pode agendar cirurgia de convênio/particular
		final boolean temPerfilMedico = getICascaFacade().usuarioTemPermissao(this.obterLoginUsuarioLogado(), "MED04");
		if (temPerfilMedico) {

			DominioGrupoConvenio valorGrupoConvenio = vo.getCirurgia().getConvenioSaudePlano().getConvenioSaude().getGrupoConvenio();

			if (valorGrupoConvenio == null) {
				throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBC_01153);
			} else if (DominioGrupoConvenio.S.equals(valorGrupoConvenio)) {
				throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBC_01154);
			}

		}

		List<MbcDescricaoCirurgica> listaDescricaoCirurgica = getMbcDescricaoCirurgicaDAO().pesquisarDescricaoCirurgicaPorCrgSeq(vo.getCirurgia().getSeq());
		if (!listaDescricaoCirurgica.isEmpty()) {
			/*
			 * CONFORME DOCUMENTO DE ANÁLISE: "REGRA DEVE SER ALTERADA NO AGHU CONFORME: Ao invés de verificar a situação, deve buscar na tabela MBC_DESCRICOES_CIRURGICAS pelo
			 * CRG_SEQ, se houverem registros então executa o bloco de código abaixo."
			 */

			// Apenas cirurgias em situação de Agendadas podem ser alteradas nesta tela
			throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBC_00467);

		}

		// Verifica alteração do código do paciente
		// Chamada da PROCEDURE MBCP_VERIF_PAC_COD_E_ATD
		this.getAgendaProcedimentosON().verificarPacienteCodigoAlterado(vo);

		final DominioOrigemPacienteCirurgia origemPacienteCirurgiaBanco = this.getMbcCirurgiasDAO().obterOrigemPacienteCirurgia(vo.getCirurgia().getSeq());

		if (!CoreUtil.igual(vo.getCirurgia().getOrigemPacienteCirurgia(), origemPacienteCirurgiaBanco)) {

			Date valordTprevIni = this.getAgendaProcedimentosFuncoesON().getConcatenacaoDataHorario(vo.getDataCirurgiaTruncada(), vo.getCirurgia().getDataPrevisaoInicio());

			// RN33: Chamada da PROCEDURE MBCK_CRG_RN.RN_CRGP_ATU_ORIGEM
			AghAtendimentos atendimentoModificado = this.getMbcCirurgiasVerificacoesRN().atualizarOrigem(vo.getCirurgia().getOrigemPacienteCirurgia(),
					vo.getCirurgia().getDataDigitacaoNotaSala(), vo.getCirurgia().getAtendimento(), vo.getCirurgia().getNaturezaAgenda(), vo.getCirurgia().getUnidadeFuncional(),
					vo.getCirurgia().getData(), vo.getCirurgia().getPaciente(), valordTprevIni, vo.getCirurgia().getEspecialidade());

			vo.getCirurgia().setAtendimento(atendimentoModificado); // Seta atendimento modificado
		}

		if (vo.getTipoPlano() != null) {

			if (DominioTipoPlano.A.equals(vo.getTipoPlano())) {

				if (!DominioOrigemPacienteCirurgia.A.equals(vo.getCirurgia().getOrigemPacienteCirurgia())) {
					// Para cirurgia com Convênio/Plano de Ambulatório origem deve ser ambulatorial
					throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBC_00506);
				}

			} else if (DominioTipoPlano.I.equals(vo.getTipoPlano())) {

				if (!DominioOrigemPacienteCirurgia.I.equals(vo.getCirurgia().getOrigemPacienteCirurgia())) {
					// Para cirurgia com Convênio/Plano de Internação origem deve ser de internação
					throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBC_00507);
				}

			}

		}

		// ATENÇÃO: A validação de tempo mínimo (PROCEDURE MBCP_VALIDA_TEMPO_MIN) foi reformulada e conforme o documento de análise: Regras do botão gravar (ID27)

		// Chamada da PROCEDURE MBCP_ALTERA_AGENDA
		MbcAgendas agenda = this.alterarAgenda(vo);

		vo.getCirurgia().setAgenda(agenda);


		// ATENÇÃO: A PROCEDURE MBCP_UTILIZACAO_SALA não será utilizada no AGHU

		return mensagemRetorno;

	}

	/**
	 * ORADB PROCEDURE MBCP_ALTERA_AGENDA
	 * <p>
	 * RN30: Altera agenda
	 * <p>
	 * 
	 * @param vo
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public MbcAgendas alterarAgenda(CirurgiaTelaVO vo) throws BaseException {

		if (vo.getCirurgia().getAgenda() == null) {
			return null;
		}

		MbcAgendas agendaRetorno = null;
		
		// Chamada da PROCEDURE MBCP_BUSCA_EQUIPE_RESP
		PesquisaQuantidadeEquipeResponsavelVO pesquisaQuantidadeEquipeResponsavelVO = this.getAgendaProcedimentosON().pesquisarQuantidadeEquipeResponsavel(
				vo.getListaProfissionaisVO());
		final int valorContResp = pesquisaQuantidadeEquipeResponsavelVO.getContaResponsaveis();

		// Chamada da PROCEDURE MBCP_BUSCA_PROC_PRINCIPAL
		PesquisaPrincipalTelaVO pesquisaPrincipalTelaVO = this.getAgendaProcedimentosON().pesquisarPrincipalTela(vo.getListaProcedimentosVO());
		final int valorContProc = pesquisaPrincipalTelaVO.getContaProcedimentos();

		if (valorContResp > 1) {
			throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBC_00949);
		}

		if (valorContProc > 1) {
			throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBC_00950);
		}

		if (valorContResp == 0) {
			throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBC_00951);
		}

		if (valorContProc == 0) {
			throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBC_00952);
		}

		Date valorDthrPrevInicio = null;
		Date valorDthrPrevFim = null;

		if (vo.getCirurgia().getDataPrevisaoInicio() == null) {
			MbcCirurgias cirurgia = this.getMbcCirurgiasDAO().obterPorChavePrimaria(vo.getCirurgia().getSeq());
			valorDthrPrevInicio = cirurgia.getDataInicioOrdem();
		} else {
			// Data de previsão inicial
			valorDthrPrevInicio = this.getAgendaProcedimentosFuncoesON().getConcatenacaoDataHorario(vo.getDataCirurgiaTruncada(), vo.getCirurgia().getDataPrevisaoInicio());
			// Data de previsão final
			valorDthrPrevFim = this.getAgendaProcedimentosFuncoesON().obterDataHoraPrevisaoFim(vo.getDataCirurgiaTruncada(), vo.getCirurgia().getTempoPrevistoHoras(),
					vo.getCirurgia().getTempoPrevistoMinutos());
		}

		// RN25: Chamada da PROCEDURE RN_CRGP_VER_BLOQUEIO
		this.getVerificaBloqueioCirurgiaRN().verificarBloqueio(vo.getDataCirurgiaTruncada(), vo.getCirurgia().getSalaCirurgica(), valorDthrPrevInicio, valorDthrPrevInicio,
				valorDthrPrevFim, pesquisaQuantidadeEquipeResponsavelVO.getProfissionalResponsavel().getMbcProfAtuaUnidCirgs());

		// RN26: Chamada da PROCEDURE MBCP_ATUALIZA_AGENDA (COM ALTERAÇÃO)
		agendaRetorno = this.getAtualizarAgendaRN().atualizarAgenda(DominioOperacaoBanco.UPD, vo.getCirurgia().getAgenda(), vo.getCirurgia().getPaciente(), vo.getCirurgia().getUnidadeFuncional(),
				vo.getCirurgia().getEspecialidade(), pesquisaQuantidadeEquipeResponsavelVO.getProfissionalResponsavel().getMbcProfAtuaUnidCirgs(),
				pesquisaPrincipalTelaVO.getProcedimentoPrincipal(), vo.getCirurgia().getTempoPrevistoHoras(), vo.getCirurgia().getTempoPrevistoMinutos(),
				vo.getCirurgia().getOrigemPacienteCirurgia(), vo.getCirurgia().getPrecaucaoEspecial(), vo.getCirurgia().getSalaCirurgica(),
				vo.getCirurgia().getDataPrevisaoInicio(), vo.getCirurgia().getConvenioSaudePlano(), vo.getDataCirurgiaTruncada(), pesquisaPrincipalTelaVO.getQtdeProc());

		return agendaRetorno;
	}

	/*
	 * Getters Facades, RNs e DAOs
	 */

	private ICascaFacade getICascaFacade() {
		return iCascaFacade;
	}
	

	private AgendaProcedimentosON getAgendaProcedimentosON() {
		return agendaProcedimentosON;
	}

	private VerificaBloqueioCirurgiaRN getVerificaBloqueioCirurgiaRN() {
		return verificaBloqueioCirurgiaRN;
	}

	private AtualizarAgendaRN getAtualizarAgendaRN() {
		return atualizarAgendaRN;
	}

	private MbcCirurgiasVerificacoesRN getMbcCirurgiasVerificacoesRN() {
		return mbcCirurgiasVerificacoesRN;
	}

	private MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}

	private MbcDescricaoCirurgicaDAO getMbcDescricaoCirurgicaDAO() {
		return mbcDescricaoCirurgicaDAO;
	}

	private AgendaProcedimentosFuncoesON getAgendaProcedimentosFuncoesON() {
		return agendaProcedimentosFuncoesON;
	}
}
