package br.gov.mec.aghu.paciente.historico.business;

import java.util.HashMap;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTipoProntuario;
import br.gov.mec.aghu.model.AipPacientesHist;
import br.gov.mec.aghu.model.AipPacientesHistJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class HistoricoPacienteJournalON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(HistoricoPacienteJournalON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IHistoricoPacienteFacade historicoPacienteFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -7778226057039724764L;

	/**
	 * Método que conduz a geração de uma entrada na tabela de journal na
	 * remoção de um histórico de paciente.
	 * 
	 * @param paciente
	 */
	public void observarPersistenciaHistoricoPaciente(
			AipPacientesHist historicoPaciente,
			AipPacientesHist historicoPacienteAnterior,
			DominioOperacoesJournal operacaoJournal) {
		//IHistoricoPacienteFacade historicoPacienteFacade = getHistoricoPacienteFacade();
		
		Map<AipPacientesHist.Fields, Object> valoresAnteriores = this
				.converterHistoricoAnteriorEmMap(historicoPacienteAnterior);

		// Comita transações pendentes para a session do hibernate
		this.flush();

		if (operacaoJournal.equals(DominioOperacoesJournal.DEL)) {
			// Gera journal do histórico de paciente
			this.gerarJournalPersistenciaHistoricoPaciente(historicoPaciente,
					valoresAnteriores, operacaoJournal);

		} else if (operacaoJournal.equals(DominioOperacoesJournal.UPD)) {
			if (this.validarGeracaoJournalPersistenciaHistoricoPaciente(
					historicoPaciente, valoresAnteriores)) {
				this.gerarJournalPersistenciaHistoricoPaciente(
						historicoPaciente, valoresAnteriores, operacaoJournal);
			}
		}

		this.flush();
	}

	/**
	 * Método para converter o objeto antigo de AipPacientesHist em um Map.
	 * 
	 * @param historicoPaciente
	 *            com dados antigos
	 * @return Map<AipPacientesHist.Fields, Object>
	 */
	private Map<AipPacientesHist.Fields, Object> converterHistoricoAnteriorEmMap(
			AipPacientesHist historicoPaciente) {
		Map<AipPacientesHist.Fields, Object> resultado = new HashMap<AipPacientesHist.Fields, Object>();

		resultado.put(AipPacientesHist.Fields.CODIGO, historicoPaciente
				.getCodigo());
		resultado.put(AipPacientesHist.Fields.PRONTUARIO, historicoPaciente
				.getProntuario());
		resultado.put(AipPacientesHist.Fields.PRNT_ATIVO, historicoPaciente
				.getPrntAtivo());

		return resultado;
	}

	/**
	 * Método responsável por avaliar a necessidade de geração de uma entrada na
	 * tabela de journal para uma atualização de um paciente. Retorna true caso
	 * seja necessário gerar o journal.
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private Boolean validarGeracaoJournalPersistenciaHistoricoPaciente(
			AipPacientesHist historicoPaciente,
			Map<AipPacientesHist.Fields, Object> valoresAnteriores) {

		if (historicoPaciente == null) {
			return false;
		}

		Integer codigoAnterior = valoresAnteriores
				.get(AipPacientesHist.Fields.CODIGO) == null ? null
				: (Integer) valoresAnteriores
						.get(AipPacientesHist.Fields.CODIGO);
		Integer prontuarioAnterior = valoresAnteriores
				.get(AipPacientesHist.Fields.PRONTUARIO) == null ? null
				: (Integer) valoresAnteriores
						.get(AipPacientesHist.Fields.PRONTUARIO);
		DominioTipoProntuario tipoProntuarioAnterior = valoresAnteriores
				.get(AipPacientesHist.Fields.PRNT_ATIVO) == null ? null
				: (DominioTipoProntuario) valoresAnteriores
						.get(AipPacientesHist.Fields.PRNT_ATIVO);

		if (this.verificarDiferencaObject(historicoPaciente.getCodigo(),
				codigoAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(historicoPaciente.getProntuario(),
				prontuarioAnterior)) {
			return true;
		}

		if (this.verificarDiferencaObject(historicoPaciente.getPrntAtivo(),
				tipoProntuarioAnterior)) {
			return true;
		}

		return false;
	}

	/**
	 * Método para fazer comparação entre valores atuais e anteriores para
	 * atributos do objeto AipPacientesHist, evitando nullpointer.
	 * 
	 * @param vlrAnterior
	 * @param vlrAtual
	 * @return FALSE se parametros recebido forem iguais / TRUE se parametros
	 *         recebidos forem diferentes
	 */
	private Boolean verificarDiferencaObject(Object vlrAtual, Object vlrAnterior) {

		if (vlrAnterior == null && vlrAtual == null) {
			return false;
		} else if (vlrAnterior != null && !(vlrAnterior.equals(vlrAtual))) {
			return true;
		} else if (vlrAtual != null && !(vlrAtual.equals(vlrAnterior))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Método responsável pela agregação e persistencia do objeto
	 * AipHistoricoPacientesJn, que representa o journal do histórico de
	 * pacientes. Esse método gera o registro de journal na inserção e
	 * atualiação de um objeto de AipPacientesHist.
	 */
	private void gerarJournalPersistenciaHistoricoPaciente(
			AipPacientesHist historicoPaciente,
			Map<AipPacientesHist.Fields, Object> valoresAnteriores,
			DominioOperacoesJournal operacaoJournal) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AipPacientesHistJn jn = BaseJournalFactory.getBaseJournal(operacaoJournal, AipPacientesHistJn.class, servidorLogado.getUsuario());

		Integer codigoAnterior = valoresAnteriores
				.get(AipPacientesHist.Fields.CODIGO) == null ? null
				: (Integer) valoresAnteriores
						.get(AipPacientesHist.Fields.CODIGO);

		Integer prontuarioAnterior = valoresAnteriores
				.get(AipPacientesHist.Fields.PRONTUARIO) == null ? null
				: (Integer) valoresAnteriores
						.get(AipPacientesHist.Fields.PRONTUARIO);

		DominioTipoProntuario tipoProntuarioAnterior = valoresAnteriores
				.get(AipPacientesHist.Fields.PRNT_ATIVO) == null ? null
				: (DominioTipoProntuario) valoresAnteriores
						.get(AipPacientesHist.Fields.PRNT_ATIVO);

		jn.setCodigo(codigoAnterior);
		jn.setProntuario(prontuarioAnterior);
		jn.setPrntAtivo(tipoProntuarioAnterior);

		this.getPacienteFacade().inserirAipPacientesHistJn(jn, false);
	}
	
	protected IHistoricoPacienteFacade getHistoricoPacienteFacade() {
		return (IHistoricoPacienteFacade) historicoPacienteFacade;
		
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return (IPacienteFacade) pacienteFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
