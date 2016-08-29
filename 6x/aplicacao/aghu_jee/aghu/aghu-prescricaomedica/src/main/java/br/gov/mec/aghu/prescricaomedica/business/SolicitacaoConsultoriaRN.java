package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioIndPendenteItemPrescricao;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoConsultoria;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoPrescricao;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoConsultoria;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmPrescricaoMedicaId;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPrescricaoMedicaDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSolicitacaoConsultoriaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * Classe de apoio para as Business Facades. Ela em geral agrupa as
 * funcionalidades encontradas em packages e procedures do AGHU.
 * 
 * Ela poderia ser uma classe com acesso friendly ou default e não ser um
 * componente seam.
 * 
 * Mas fazendo assim facilita, pois ela também pode receber uma referência para
 * o EntityManager.
 * 
 * Outra forma de fazer é instanciar ela diretamente do ON e passar o entity
 * manager para seus parâmetros. Neste caso os metodos desta classe poderiam ser
 * até estaticos e nao necessitar de instanciação. Ai ela seria apenas um
 * particionamento lógico de código e não um componente que possa ser injetado
 * em qualquer outro contexto.
 * 
 * ATENÇÃO, Os metodos desta classe nunca devem ser acessados diretamente por
 * qualquer classe que não a ON correspondente. Por isso sugere-se que todos os
 * métodos desta sejam friendly (default) ou private.
 * 
 */
@SuppressWarnings("PMD.JUnit4TestShouldUseTestAnnotation")
@Stateless
public class SolicitacaoConsultoriaRN extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(SolicitacaoConsultoriaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmPrescricaoMedicaDAO mpmPrescricaoMedicaDAO;

@Inject
private MpmSolicitacaoConsultoriaDAO mpmSolicitacaoConsultoriaDAO;
	/**
	 * 
	 */
	private static final long serialVersionUID = -2582298282068104039L;

	/**
	 * Possíveis códigos de erro retornados pela PACKAGE MPMK_SCN_RN MPA-00900 =
	 * Erro update MPA_USO_CONSULTORIAS na regra RN_SCNP_DEL_UOT_PROT. Contate
	 * GSIS. {0} MPA-00901 = Erro delete MPA_USO_ORD_CONSULTORIAS na regra
	 * RN_SCNP_DEL_UOT_PROT. Contate GSIS. {0} MPM-00722 = Especialidade deve
	 * estar ativa. MPM-00723 = Não é mais permitida a modificação na
	 * solicitação de consultoria. MPM-00724 = Resposta da consultoria deve ser
	 * informada quando o tipo de resposta for obrigatório: {0} MPM-00725 =
	 * Dever ser preenchido pelo menos uma resposta para uma solicitação de
	 * consultoria. MPM-00726 = Não é permitido reativar uma solicitação
	 * inativa. MPM-01668 = Não é possível alterar a especialidade. MPM-01972 =
	 * Não excluir Solicitação de Consultorias quando já estiver validada.
	 * MPM-03734 = Erro na inclusão da mensagem da caixa postal para solicitação
	 * de consultoria. RAP-00175 = Não existe Servidor cadastrado com
	 * matricula/vinculo.
	 * 
	 * 
	 * TODO O codigo dessa enumeracao esta mantendo o codigo da CGTI apenas como
	 * exemplo, modificar para algo que facao mais sentido para o AGHU. TODO
	 * Mudar as mensagens para referenciar o nome do respectivo método de
	 * negócio desta classe de negócio
	 */
	private enum SolicitacaoConsultoriaRNExceptionCode implements
			BusinessExceptionCode {
		MPA_00900, SCN_NAO_EXISTE_PRESCRICAO_MEDICA, SCN_TERMINO_ITEM_PRESCRICAO_POSTERIOR, SCN_CONFIRMADA_OUTRO_USUARIO, RN_PENDENTE
	}


	/**
	 * Método que verifica a existencia de uma prescrição médica para esta
	 * solicitação quando a consultoria for de origem médica. A solicitação de
	 * consultoria médica deve estar no período de uma prescrição para o mesmo
	 * atendimento Método responsável por implementar a procedure
	 * MPMK_SCN_RN.RN_SCNP_VER_PRESCR.
	 * 
	 * @param newAtdSeq
	 * @param newInicio
	 * @param newFim
	 * @param mvtoPendente
	 * @param indPendente
	 * @param oper
	 * @param pOrigem
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public DominioOrigemSolicitacaoConsultoria verificarExistenciaPrescricao(
			Integer newAtdSeq, Date newInicio, Date newFim, Date mvtoPendente,
			DominioIndPendenteItemPrescricao indPendente, String oper,
			DominioOrigemSolicitacaoConsultoria pOrigem)
			throws ApplicationBusinessException {
		// ORADB MPMK_SCN_RN.RN_SCNP_VER_PRESCR

		if (pOrigem == null
				|| pOrigem.equals(DominioOrigemSolicitacaoConsultoria.M)) {
			verificarPrescricao(newAtdSeq, newInicio, newFim, mvtoPendente,
					indPendente, oper);
		}

		return pOrigem;
	}

	/**
	 * Método que obtém uma Prescrição Médica Método responsável por implementar
	 * a procedure MPMK_RN.MPMP_VER_PRCR_MDC.
	 * 
	 * @param newAtdSeq
	 * @param newInicio
	 * @param newFim
	 * @param mvtoPendente
	 * @param indPendente
	 * @param oper
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private void verificarPrescricao(Integer newAtdSeq, Date newInicio,
			Date newFim, Date mvtoPendente,
			DominioIndPendenteItemPrescricao indPendente, String oper)
			throws ApplicationBusinessException {

		MpmPrescricaoMedica prescricaoMedica = obterPrescricao(newAtdSeq,
				newInicio, newFim, oper, indPendente);

		if (prescricaoMedica == null) {
			throw new ApplicationBusinessException(
					SolicitacaoConsultoriaRNExceptionCode.SCN_NAO_EXISTE_PRESCRICAO_MEDICA);
		}

		/*
		 * guarda o seq da prescricao a alterar e a data a testar (FORA DO
		 * ESCOPO DA PROVA DE CONCEITO)
		 */
		// Integer seqSalvo = prescricaoMedica.getId().getSeq();
		// Date dtHrMvtoSalvo = prescricaoMedica.getDthrInicioMvtoPendente();
		// Date dtHrMovimentoSalvo = prescricaoMedica.getDthrMovimento();

		if (newFim != null && newFim.after(prescricaoMedica.getDthrFim())) {

			/* buscando prescrição válida para a data final informada */
			prescricaoMedica = obterPrescricao(newAtdSeq, newInicio, newFim,
					oper, indPendente);

			if (prescricaoMedica == null) {
				throw new ApplicationBusinessException(
						SolicitacaoConsultoriaRNExceptionCode.SCN_TERMINO_ITEM_PRESCRICAO_POSTERIOR);
			}
		}

		/* Faz Update na Prescrição Médica (FORA DO ESCOPO DA PROVA DE CONCEITO) */
		// if (dtHrMvtoSalvo == null && !indPendente.equals("N")) {
		//	
		// prescricaoMedica = obterPrescricao(seqSalvo, newAtdSeq);
		// prescricaoMedica.setDthrInicioMvtoPendente(dtHrMovimentoSalvo);
		// entityManager.persist(prescricaoMedica);
		//
		// }
	}

	/**
	 * Método que recupera uma Prescrição Médica Método responsável por
	 * implementar a procedure MPMK_RN.MPMP_GET_PRCR_MED2.
	 * 
	 * @param newAtdSeq
	 * @param newInicio
	 * @param newFim
	 * @param oper
	 * @param indPendente
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private MpmPrescricaoMedica obterPrescricao(Integer newAtdSeq,
			Date newInicio, Date newFim, String oper,
			DominioIndPendenteItemPrescricao indPendente)
			throws ApplicationBusinessException {

		MpmPrescricaoMedica prescricaoMedica = null;

		if (newInicio == null) {
			// Exception MPM-02427
			throw new ApplicationBusinessException(
					SolicitacaoConsultoriaRNExceptionCode.SCN_CONFIRMADA_OUTRO_USUARIO);
		}

		if (newFim == null
				|| (newInicio != null && newInicio.compareTo(newFim) == 0)) {

			prescricaoMedica = obterPrescricaoMedicaPorAtendimentoEDataInicio(
					newAtdSeq, newInicio);

			if (prescricaoMedica != null) {
				testarPrescricaoEmUso(indPendente, newFim, oper,
						prescricaoMedica.getSituacao(), prescricaoMedica
								.getDthrMovimento(), prescricaoMedica
								.getDthrFim(), 1);
			}

		} else {

			if (oper.equals("I")) {
				prescricaoMedica = obterPrescricaoMedicaPorAtendimentoEDataInicio(
						newAtdSeq, newInicio);

				if (prescricaoMedica == null) {
					return null;
				} else {
					testarPrescricaoEmUso(indPendente, newFim, oper,
							prescricaoMedica.getSituacao(), prescricaoMedica
									.getDthrMovimento(), prescricaoMedica
									.getDthrFim(), 2);
				}
			}
			if (oper.equals("U")
					|| (prescricaoMedica != null && newFim != null
							&& prescricaoMedica.getDthrFim() != null && newFim
							.after(prescricaoMedica.getDthrFim()))) {

				// Obtém a prescrição pela data de fim
				prescricaoMedica = obterPrescricaoMedicaPorAtendimentoEDataFim(
						newAtdSeq, newFim);
				if (prescricaoMedica == null) {
					return null;
				} else {
					testarPrescricaoEmUso(indPendente, newFim, oper,
							prescricaoMedica.getSituacao(), prescricaoMedica
									.getDthrMovimento(), prescricaoMedica
									.getDthrFim(), 3);
				}
			}
		}

		// Retorna a Prescrição
		return prescricaoMedica;

	}

	/**
	 * Método que obtém uma Prescrição Médica pelo id do atendimento e a data de
	 * Início
	 * 
	 * @param atdSeq
	 * @param dataInicio
	 * @return MpmPrescricaoMedicas
	 * @throws ApplicationBusinessException
	 */
	private MpmPrescricaoMedica obterPrescricaoMedicaPorAtendimentoEDataInicio(
			Integer atdSeq, Date dataInicio) {
		return this.getMpmPrescricaoMedicaDAO().obterPrescricaoMedicaPorAtendimentoEDataInicio(atdSeq, dataInicio);
	}

	/**
	 * Método que obtém uma Prescrição Médica pelo id do atendimento e a data de
	 * Fim
	 * 
	 * @param atdSeq
	 * @param dataFim
	 * @return MpmPrescricaoMedicas
	 * @throws ApplicationBusinessException
	 */
	private MpmPrescricaoMedica obterPrescricaoMedicaPorAtendimentoEDataFim(
			Integer atdSeq, Date dataFim) {
		return this.getMpmPrescricaoMedicaDAO().obterPrescricaoMedicaPorAtendimentoEDataFim(atdSeq, dataFim);
	}

	/**
	 * Método que testa se uma Prescrição Médica está em uso Equivalente à
	 * procedure MPMK_RN.MPMP_GET_PRCR_MED2.mpmp_testa_em_uso
	 * 
	 * @param indPendente
	 * @param dthFim
	 * @param oper
	 * @param situacao
	 * @param dthMovimento
	 * @param dthFimPME
	 * @param seq
	 * @return
	 * @throws ApplicationBusinessException
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void testarPrescricaoEmUso(
			DominioIndPendenteItemPrescricao indPendente, Date dthFim,
			String oper, DominioSituacaoPrescricao situacao, Date dthMovimento,
			Date dthFimPME, Integer seq) throws ApplicationBusinessException {

		Boolean fazerTeste = true;

		// TODO Retirar o .toString do DominioIndPendenciaSolicitacaoConsultoria
		if (indPendente == null || indPendente.toString().equals("D")
				|| indPendente.equals(DominioIndPendenteItemPrescricao.N)) {
			fazerTeste = false;
		}
		if (dthFim != null && (dthFim.compareTo(new Date()) <= 0)) {
			fazerTeste = false;
		}
		if (oper.equals("U")
				&& indPendente.equals(DominioIndPendenteItemPrescricao.P)
				&& dthFim != null) {
			fazerTeste = false;
		}
		if (oper.equals("U")
				&& (indPendente.equals(DominioIndPendenteItemPrescricao.E) || indPendente
						.equals(DominioIndPendenteItemPrescricao.A))
				&& dthFim.compareTo(dthFimPME) == 0) {
			fazerTeste = false;
		}

		if (fazerTeste) {
			if (!situacao.equals(DominioSituacaoPrescricao.U)
					|| dthMovimento == null) {
				if (seq == 1 || seq == 2 || seq == 3) {
					throw new ApplicationBusinessException(
							SolicitacaoConsultoriaRNExceptionCode.SCN_CONFIRMADA_OUTRO_USUARIO);
				}
			}
		}
	}

	/**
	 * 
	 * Método usado para obter uma prescrição médica através dos valores que
	 * compões sua chave primária.
	 * 
	 * @param idAtendimento
	 * @param idPrescricao
	 * @return
	 */
	public MpmPrescricaoMedica obterPrescricaoPorChave(Integer idAtendimento,
			Integer idPrescricao) {
		MpmPrescricaoMedicaId prescricaoID = new MpmPrescricaoMedicaId();
		prescricaoID.setAtdSeq(idAtendimento);
		prescricaoID.setSeq(idPrescricao);

		MpmPrescricaoMedica prescricao = this.getMpmPrescricaoMedicaDAO().obterPorChavePrimaria(prescricaoID);

		return prescricao;
	}
	
	
	/**
	 * Metodo para verificar se ocorreu modificacao para o Objeto MpmSolicitacaoConsultoria.<br>
	 * NAO executa load da Entidade MpmSolicitacaoConsultoria.<br>
	 * Compara os campos que podem ser alterados pelo Usuario,
	 * na tela de Solicitar Consultoria.<br>
	 * O parametro <b>consultoria</b> deve ser informado e deve existir no banco de dados.<br> 
	 * 
	 * @param consultoria
	 * @return retorno <code>true</code> algum campo foi alterado. E <code>false</code> para os outros casos.
	 * @throws IllegalArgumentException
	 * @throws IllegalStateException
	 */
	public boolean hasModificacao(MpmSolicitacaoConsultoria consultoria){
		Boolean retorno = false;
		
		if (consultoria == null) {
			throw new IllegalArgumentException("Parametro Invalido: deve ser informado uma entidade.");
		}
		if (consultoria.getId() == null) {
			throw new IllegalStateException("A entidade nao possui Id, nao pode ser feito teste de modificacao.");
		}
		
		MpmSolicitacaoConsultoriaDAO solicitacaoConsultoriaDAO = getSolicitacaoConsultoriaDAO();
		List<Object[]> listaObjetos = solicitacaoConsultoriaDAO.buscarValoresConsultoriaAnterior(consultoria);
		if (listaObjetos.size() > 0){
			Object[] vetorObject = listaObjetos.get(0);
			Short espSeq = (Short)vetorObject[0];
			DominioTipoSolicitacaoConsultoria tipo = (DominioTipoSolicitacaoConsultoria)vetorObject[1];
			DominioSimNao urgente = (DominioSimNao)vetorObject[2];
			String motivo = (String)vetorObject[3];
			
			if ((CoreUtil.modificados(consultoria.getEspecialidade().getSeq(),espSeq))
					|| (CoreUtil.modificados(consultoria.getTipo(),tipo))
					|| (CoreUtil.modificados(consultoria.getIndUrgencia(),urgente))
					|| (CoreUtil.modificados(consultoria.getMotivo(),motivo))) {
				retorno = true;
			}
		}
		
		return retorno;
	}
	
	private MpmSolicitacaoConsultoriaDAO getSolicitacaoConsultoriaDAO(){
		return mpmSolicitacaoConsultoriaDAO;
	}
	
	private MpmPrescricaoMedicaDAO getMpmPrescricaoMedicaDAO(){
		return mpmPrescricaoMedicaDAO;
	}

}