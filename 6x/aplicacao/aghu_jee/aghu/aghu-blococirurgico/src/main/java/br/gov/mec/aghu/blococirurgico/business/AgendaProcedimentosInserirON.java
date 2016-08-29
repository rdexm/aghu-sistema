package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.AgendaProcedimentosON.AgendaProcedimentosONExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaVO;
import br.gov.mec.aghu.blococirurgico.vo.PesquisaPrincipalTelaVO;
import br.gov.mec.aghu.blococirurgico.vo.PesquisaQuantidadeEquipeResponsavelVO;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioOperacaoBanco;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * Classe responsável pelas regras do ATUALIZAR FORMS de #22460: Agendar procedimentos eletivo, urgência ou emergência Classe criada para suprir as violações de PMD (tamanho de
 * classe) que ocorriam na classe AgendaProcedimentosON
 * 
 * @author aghu
 * 
 */

@Stateless
public class AgendaProcedimentosInserirON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AgendaProcedimentosInserirON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	
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
	private AgendaProcedimentosAtualizarON agendaProcedimentosAtualizarON;

	/**
	 * 
	 */
	private static final long serialVersionUID = -6965049166489927899L;

	/**
	 * ORADB PROCEDURE EVT_PRE_INSERT
	 * <p>
	 * RN21
	 * <p>
	 * 
	 * @param vo
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public String preInsert(CirurgiaTelaVO vo) throws BaseException {

		String mensagemRetorno = null; // Retorno da mensagem de confirmação na tela

		/*
		 * ATENÇÃO: O parâmetro P_SPE_SEQ é setado no inicio da controller
		 */

		vo.getCirurgia().setTemDescricao(null);

		// Se o usuário é médico, só pode agendar cirurgia de convênio/particular
		final boolean temPerfilMedico = getICascaFacade().usuarioTemPermissao(this.obterLoginUsuarioLogado(), "MED04");
		final boolean temPerfilMedicoBlocoCirurgico = getICascaFacade().usuarioTemPermissao(this.obterLoginUsuarioLogado(), "MBCG_MEDICO");

		if (temPerfilMedico || temPerfilMedicoBlocoCirurgico) {

			DominioGrupoConvenio valorGrupoConvenio = vo.getCirurgia().getConvenioSaudePlano().getConvenioSaude().getGrupoConvenio();

			if (valorGrupoConvenio == null) {
				throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBC_01151);
			} else if (DominioGrupoConvenio.S.equals(valorGrupoConvenio)) {
				throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBC_01152);
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

		} else {

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

		}

		// ATENÇÃO: A validação de tempo mínimo (PROCEDURE MBCP_VALIDA_TEMPO_MIN) foi reformulada e conforme o documento de análise: Regras do botão gravar (ID27)

		// Chamada da PROCEDURE MBCP_INSERE_AGENDA
		this.inserirAgenda(vo);
		// Chamada da PROCEDURE MBCP_ALTERA_AGENDA
		this.getAgendaProcedimentosAtualizarON().alterarAgenda(vo);

		// ATENÇÃO: A PROCEDURE MBCP_UTILIZACAO_SALA não será utilizada no AGHU

		return mensagemRetorno;

	}

	/**
	 * ORADB PROCEDURE MBCP_INSERE_AGENDA
	 * 
	 * <p>
	 * RN27: Insere agenda
	 * <p>
	 * 
	 * @param vo
	 * @param servidorLogado
	 * @throws BaseException
	 */
	public void inserirAgenda(CirurgiaTelaVO vo) throws BaseException {

		// Chamada da PROCEDURE MBCP_BUSCA_EQUIPE_RESP
		PesquisaQuantidadeEquipeResponsavelVO pesquisaQuantidadeEquipeResponsavelVO = this.getAgendaProcedimentosON().pesquisarQuantidadeEquipeResponsavel(
				vo.getListaProfissionaisVO());
		final int valorContResp = pesquisaQuantidadeEquipeResponsavelVO.getContaResponsaveis();

		// Chamada da PROCEDURE MBCP_BUSCA_PROC_PRINCIPAL
		PesquisaPrincipalTelaVO pesquisaPrincipalTelaVO = this.getAgendaProcedimentosON().pesquisarPrincipalTela(vo.getListaProcedimentosVO());
		final int valorContProc = pesquisaPrincipalTelaVO.getContaProcedimentos();

		if (valorContResp > 1) {
			// Assinale apenas um profissional para responsável pela cirurgia
			throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBC_00949);
		}

		if (valorContProc > 1) {
			// Assinale apenas um procedimento como sendo principal da cirurgia
			throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBC_00950);
		}

		if (valorContResp == 0) {
			// Informe/assinale o profissional para responsável pela cirurgia
			throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBC_00951);
		}

		if (valorContProc == 0) {
			// Informe/assinale o procedimento principal da cirurgia
			throw new ApplicationBusinessException(AgendaProcedimentosONExceptionCode.MBC_00952);
		}

		// Data de previsão inicial
		Date valorDthrPrevInicio = this.getAgendaProcedimentosFuncoesON().getConcatenacaoDataHorario(vo.getDataCirurgiaTruncada(), vo.getCirurgia().getDataPrevisaoInicio());
		// Data de previsão final
		Date valorDthrPrevFim = this.getAgendaProcedimentosFuncoesON().obterDataHoraPrevisaoFim(valorDthrPrevInicio, vo.getCirurgia().getTempoPrevistoHoras(),
				vo.getCirurgia().getTempoPrevistoMinutos());

		// RN25: Chamada da PROCEDURE RN_CRGP_VER_BLOQUEIO
		this.getVerificaBloqueioCirurgiaRN().verificarBloqueio(vo.getDataCirurgiaTruncada(), vo.getCirurgia().getSalaCirurgica(), valorDthrPrevInicio, valorDthrPrevInicio,
				valorDthrPrevFim, pesquisaQuantidadeEquipeResponsavelVO.getProfissionalResponsavel().getMbcProfAtuaUnidCirgs());

		// RN26: Chamada da PROCEDURE MBCP_ATUALIZA_AGENDA (COM INSERIR)
		MbcAgendas agenda = getAtualizarAgendaRN().atualizarAgenda(DominioOperacaoBanco.INS, null, vo.getCirurgia().getPaciente(), vo.getCirurgia().getUnidadeFuncional(),
				vo.getCirurgia().getEspecialidade(), pesquisaQuantidadeEquipeResponsavelVO.getProfissionalResponsavel().getMbcProfAtuaUnidCirgs(),
				pesquisaPrincipalTelaVO.getProcedimentoPrincipal(), vo.getCirurgia().getTempoPrevistoHoras(), vo.getCirurgia().getTempoPrevistoMinutos(),
				vo.getCirurgia().getOrigemPacienteCirurgia(), vo.getCirurgia().getPrecaucaoEspecial(), vo.getCirurgia().getSalaCirurgica(),
				vo.getCirurgia().getDataPrevisaoInicio(), vo.getCirurgia().getConvenioSaudePlano(), vo.getDataCirurgiaTruncada(), pesquisaPrincipalTelaVO.getQtdeProc());

		vo.getCirurgia().setAgenda(agenda); // Seta agenda

		// ATENÇÃO: A chamada da PROCEDURE MBCC_UTILIZACAO_SALA foi desconsiderada no AGHU conforme o documento de análise.

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

	private AgendaProcedimentosAtualizarON getAgendaProcedimentosAtualizarON() {
		return agendaProcedimentosAtualizarON;
	}

	private VerificaBloqueioCirurgiaRN getVerificaBloqueioCirurgiaRN() {
		return verificaBloqueioCirurgiaRN;
	}

	private AtualizarAgendaRN getAtualizarAgendaRN() {
		return atualizarAgendaRN;
	}

	private MbcDescricaoCirurgicaDAO getMbcDescricaoCirurgicaDAO() {
		return mbcDescricaoCirurgicaDAO;
	}

	private AgendaProcedimentosFuncoesON getAgendaProcedimentosFuncoesON() {
		return agendaProcedimentosFuncoesON;
	}

}
