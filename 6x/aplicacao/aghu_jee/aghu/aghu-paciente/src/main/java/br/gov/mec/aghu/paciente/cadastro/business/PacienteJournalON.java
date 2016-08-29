package br.gov.mec.aghu.paciente.cadastro.business;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoProntuario;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipPacientesHistJn;
import br.gov.mec.aghu.model.AipPacientesJn;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesJnDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * O propósito desta classe é prover regras para criação do journal de paciente.
 */
@Stateless
public class PacienteJournalON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PacienteJournalON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@Inject
	private AipPacientesJnDAO aipPacientesJnDAO;
	
	@EJB
	private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7454986793778795391L;

	private enum PacienteJournalONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_USUARIO_NAO_CADASTRADO_SERVIDOR, AIP_00222;

		public void throwException() throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this);
		}
	}

	/**
	 * Método que conduz a geração de uma entrada na tabela de journal na
	 * atualização de um paciente.
	 * 
	 * @param paciente
	 */
	public AipPacientes observarAtualizacaoPaciente(AipPacientes pacienteAnterior, AipPacientes pacienteAtual, DominioOperacoesJournal operacao) {

		Map<AipPacientes.Fields, Object> valoresAnteriores = obterValoresAnterioresPaciente(pacienteAnterior);

		if (DominioOperacoesJournal.DEL.equals(operacao)) {
			gerarJournalPacienteAtualizacao(pacienteAtual, pacienteAnterior, valoresAnteriores, operacao);

		} else if (DominioOperacoesJournal.UPD.equals(operacao)) {

			if (this.validarGeracaoJournalPacienteAtualizacao(pacienteAtual, valoresAnteriores)) {
				this.gerarJournalPacienteAtualizacao(pacienteAtual, pacienteAnterior, valoresAnteriores, operacao);

				// Salva o registro atual (que só pode ser salvo após o journal)
				AipPacientesDAO aipPacientesDAO = this.getAipPacientesDAO();

				aipPacientesDAO.desatachar(pacienteAnterior);
				pacienteAtual = aipPacientesDAO.merge(pacienteAtual);
			}
		}
		
		return pacienteAtual;
	}

	/**
	 * método responsável pela agregação e persistencia do objeto
	 * AipPacienteJn,que representa o journal do paciente, na sua atualização.
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	private void gerarJournalPacienteAtualizacao(AipPacientes paciente,
			AipPacientes pacienteAnterior,
			Map<AipPacientes.Fields, Object> valoresAnteriores,
			DominioOperacoesJournal operacao) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AipPacientesJn jn = BaseJournalFactory.getBaseJournal(operacao, AipPacientesJn.class, servidorLogado.getUsuario());
		
		Integer prontuarioAnterior = (Integer) valoresAnteriores.get(AipPacientes.Fields.PRONTUARIO);

		String nomeAnterior = (String) valoresAnteriores.get(AipPacientes.Fields.NOME);

		Date dtNascimentoAnterior = (Date) valoresAnteriores.get(AipPacientes.Fields.DATA_NASCIMENTO);

		Date dtIdentificacaoAnterior = (Date) valoresAnteriores.get(AipPacientes.Fields.DATA_IDENTIFICACAO);

		Integer codigoAnterior = (Integer) valoresAnteriores.get(AipPacientes.Fields.CODIGO);

		DominioTipoProntuario prntAtivoAnterior = (DominioTipoProntuario) valoresAnteriores.get(AipPacientes.Fields.PRN_ATIVO);

		Short volumeAnterior = (Short) valoresAnteriores.get(AipPacientes.Fields.VOLUMES);

		RapServidores servidorCadastroAnterior = (RapServidores) valoresAnteriores.get(AipPacientes.Fields.SERVIDOR_CADASTRO);

		RapServidores servidorRecadastroAnterior = (RapServidores) valoresAnteriores.get(AipPacientes.Fields.SERVIDOR_RECADASTRO);

		FccCentroCustos centroCustoCadastroAnterior = (FccCentroCustos) valoresAnteriores.get(AipPacientes.Fields.CENTRO_CUSTO_CADASTRO);

		FccCentroCustos centroCustoRecadastroAnterior = (FccCentroCustos) valoresAnteriores.get(AipPacientes.Fields.CENTRO_CUSTO_RECADASTRO);

		DominioSexo sexoAnterior = (DominioSexo) valoresAnteriores.get(AipPacientes.Fields.SEXO_BIOLOGICO);

		DominioSimNao indPacAgfaAnterior = (DominioSimNao) valoresAnteriores.get(AipPacientes.Fields.IND_PACIENTE_AGFA);

		// Seta demais campos
		jn.setCodigoPaciente(codigoAnterior);
		jn.setPrntAtivo(prntAtivoAnterior);

		if (servidorCadastroAnterior != null) {
			jn.setMatriculaServidorCadastro(servidorCadastroAnterior.getId().getMatricula());
			jn.setVinCodigoServidorCadastro(servidorCadastroAnterior.getId().getVinCodigo());
		}
		
		if (servidorRecadastroAnterior != null) {
			jn.setMatriculaServidorRecadastro(servidorRecadastroAnterior.getId().getMatricula());
			jn.setVinCodigoServidorRecadastro(servidorRecadastroAnterior.getId().getVinCodigo());
		}
		
		if (centroCustoCadastroAnterior != null) {
			jn.setCodigoCentroCustoCadastro(centroCustoCadastroAnterior.getCodigo());
		}
		
		if (centroCustoRecadastroAnterior != null) {
			jn.setCodigoCentroCustoRecadastro(centroCustoRecadastroAnterior.getCodigo());
		}
		

		// Casos específicos para o Journal de UPDATE
		if (DominioOperacoesJournal.UPD.equals(operacao)) {
			jn.setVolumes(volumeAnterior);
			jn.setSexoBiologico(sexoAnterior);
			jn.setIndPacAgfa(indPacAgfaAnterior);

			if (prontuarioAnterior != null
					&& !Objects.equals(nomeAnterior, paciente.getNome())) {
				jn.setNomePaciente(nomeAnterior);
			} else {
				jn.setNomePaciente(null);
			}

			if (!Objects.equals(prontuarioAnterior, paciente
					.getProntuario())) {
				jn.setProntuario(prontuarioAnterior);
			} else {
				jn.setProntuario(null);
			}

			if (prontuarioAnterior != null && paciente.getProntuario() == null) {
				jn.setDtIdentificacao(dtIdentificacaoAnterior);
			} else {
				jn.setDtIdentificacao(null);
			}

			if (!Objects.equals(dtNascimentoAnterior, paciente
					.getDtNascimento())) {
				jn.setDtNascimento(dtNascimentoAnterior);
			} else {
				jn.setDtNascimento(null);
			}
		}
		else{
			// Se a operação for Delete somente seta estes campos
			jn.setNomePaciente(nomeAnterior);
			jn.setProntuario(prontuarioAnterior);
			jn.setDtIdentificacao(dtIdentificacaoAnterior);
			jn.setDtNascimento(dtNascimentoAnterior);
		}

		this.getAipPacientesJnDAO().persistir(jn);
	}

	/**
	 * Método responsável por avaliar a necessidade de geração de uma entrada na
	 * tabela de journal para uma atualização de um paciente. Retorna true caso
	 * seja necessário gerar o journal.
	 * 
	 * @return
	 */
	private boolean validarGeracaoJournalPacienteAtualizacao(
			AipPacientes paciente,
			Map<AipPacientes.Fields, Object> valoresAnteriores) {

		Integer codigoAnterior = (Integer) valoresAnteriores
				.get(AipPacientes.Fields.CODIGO);

		Integer prontuarioAnterior = (Integer) valoresAnteriores
				.get(AipPacientes.Fields.PRONTUARIO);

		String nomeAnterior = (String) valoresAnteriores
				.get(AipPacientes.Fields.NOME);

		Date dtNascimentoAnterior = (Date) valoresAnteriores
				.get(AipPacientes.Fields.DATA_NASCIMENTO);

		DominioTipoProntuario prnAtivoAnterior = null;
		if (valoresAnteriores.get(AipPacientes.Fields.PRN_ATIVO) != null) {
			prnAtivoAnterior = (DominioTipoProntuario) valoresAnteriores
					.get(AipPacientes.Fields.PRN_ATIVO);
		}

		Short volumeAnterior = (Short) valoresAnteriores
				.get(AipPacientes.Fields.VOLUMES);

		if (!Objects.equals(codigoAnterior, paciente.getCodigo())) {
			return true;
		}

		if (!Objects.equals(nomeAnterior, paciente.getNome())) {
			return true;
		}

		if (!Objects.equals(prontuarioAnterior, paciente.getProntuario())) {
			return true;
		}

		if (!Objects.equals(prnAtivoAnterior, paciente.getPrntAtivo())) {
			return true;
		}

		if (!Objects.equals(volumeAnterior, paciente.getVolumes())) {
			return true;
		}

		if (verificarDiferencaDatas(dtNascimentoAnterior, paciente
				.getDtNascimento())) {
			return true;
		}
		return false;
	}

	private Map<AipPacientes.Fields, Object> obterValoresAnterioresPaciente(
			AipPacientes paciente) {

		Map<AipPacientes.Fields, Object> resultado = new HashMap<AipPacientes.Fields, Object>();

		resultado.put(AipPacientes.Fields.CODIGO, paciente.getCodigo());
		resultado.put(AipPacientes.Fields.NOME, paciente.getNome());
		resultado.put(AipPacientes.Fields.PRONTUARIO, paciente.getProntuario());
		resultado.put(AipPacientes.Fields.DATA_NASCIMENTO, paciente
				.getDtNascimento());
		resultado.put(AipPacientes.Fields.PRN_ATIVO, paciente.getPrntAtivo());
		resultado.put(AipPacientes.Fields.VOLUMES, paciente.getVolumes());
		resultado.put(AipPacientes.Fields.DATA_IDENTIFICACAO, paciente
				.getDtIdentificacao());

		resultado.put(AipPacientes.Fields.SERVIDOR_CADASTRO, paciente
				.getRapServidoresCadastro());
		resultado.put(AipPacientes.Fields.SERVIDOR_RECADASTRO, paciente
				.getRapServidoresRecadastro());

		resultado.put(AipPacientes.Fields.CENTRO_CUSTO_CADASTRO, paciente
				.getFccCentroCustosCadastro());
		resultado.put(AipPacientes.Fields.CENTRO_CUSTO_RECADASTRO, paciente
				.getFccCentroCustosRecadastro());

		resultado.put(AipPacientes.Fields.SEXO_BIOLOGICO, paciente
				.getSexoBiologico());
		resultado.put(AipPacientes.Fields.IND_PACIENTE_AGFA, paciente
				.getIndPacAgfa());

		return resultado;
	}

	/**
	 * Método para buscar último journal do paciente referente a uma operação de
	 * update.
	 * 
	 * @param codigo
	 * @param operacao
	 * @return
	 */
	public AipPacientesHistJn obterPacienteJournalHist(Integer codigo, DominioOperacoesJournal operacao) {
		AipPacientesJnDAO aipPacientesJnDAO = this.getAipPacientesJnDAO();
		Date dataAlteracao = aipPacientesJnDAO.buscaMaiorDataPacienteHistJn(codigo, operacao);

		if (dataAlteracao == null) {
			return null;
		} else {
			// Busca ultimo registro inserido através da data de alteração
			return aipPacientesJnDAO.buscaUltimoRegistroPorDataAlteracaoHist(codigo, operacao, dataAlteracao);
		}
	}
	
	/**
	 * Método para buscar último journal do paciente referente a uma operação de
	 * update.
	 * 
	 * @param codigo
	 * @param operacao
	 * @return
	 */
	public AipPacientesJn obterPacienteJournal(Integer codigo, DominioOperacoesJournal operacao) {
		AipPacientesJnDAO aipPacientesJnDAO = this.getAipPacientesJnDAO();
		Date dataAlteracao = aipPacientesJnDAO.buscaMaiorDataPacienteJn(codigo, operacao);

		if (dataAlteracao == null) {
			return null;
		} else {
			// Busca ultimo registro inserido através da data de alteração
			return aipPacientesJnDAO.buscaUltimoRegistroPorDataAlteracao(codigo, operacao, dataAlteracao);
		}
	}
	
	/**
	 * <h1>Conversão da procedure insere_journal</h1> Insere um novo registro na
	 * tabela AIP_PACIENTES_JN através dos parâmetros do método informados. <br/>
	 * <br/>
	 * 
	 * ORADB Procedure insere_journal, encontrada dentro da procedure
	 * AIPP_SUBSTITUI_PRONT.
	 * 
	 * TODO remover o nome procedure e deixar só insereJournal para seguir padrão
	 * 
	 * @author mtocchetto
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	public void inserirJournal(Integer pProntuarioOrigem,
			Integer pCodigoDestino) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		IAghuFacade aghuFacade = this.getAghuFacade();
		
		Integer vSerMatriculaRecadastro = servidorLogado.getId().getMatricula();
		Short vSerVinCodigoRecadastro = servidorLogado.getId().getVinCodigo();
		Integer vCctCodigoRecadastro = aghuFacade.getCcustAtuacao(servidorLogado);

		if (vSerMatriculaRecadastro == null) {
			PacienteJournalONExceptionCode.MENSAGEM_ERRO_USUARIO_NAO_CADASTRADO_SERVIDOR
					.throwException();
		}

		try {
			AipPacientesJn jn = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, AipPacientesJn.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
			
			String nome = this.getAipPacientesDAO().cursorCBuscaNome(pProntuarioOrigem);
			
			jn.setCodigoPaciente(pCodigoDestino);
			jn.setNomePaciente(nome);
			jn.setProntuario(pProntuarioOrigem);
			jn.setMatriculaServidorRecadastro(vSerMatriculaRecadastro);
			jn.setVinCodigoServidorRecadastro(vSerVinCodigoRecadastro);
			jn.setCodigoCentroCustoRecadastro(vCctCodigoRecadastro);

			this.getAipPacientesJnDAO().persistir(jn);
			this.getAipPacientesJnDAO().flush();
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			PacienteJournalONExceptionCode.AIP_00222.throwException();
		}
	}

	/**
	 * TODO Este método deverá ficar centralizado Método para fazer comparação
	 * entre valores atuais e anteriores para atributos do tipo Data, evitando
	 * nullpointer.
	 * 
	 * @param data1
	 * @param data2
	 * @return FALSE se parametros recebido forem iguais / TRUE se parametros
	 *         recebidos forem diferentes
	 */
	public boolean verificarDiferencaDatas(Date data1, Date data2) {
		if (data1 == null && data2 == null) {
			return false;
		} else if (data1 == null && data2 != null) {
			return true;
		} else if (data2 == null && data1 != null) {
			return true;
		} else if (data1.compareTo(data2) != 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Método responsável por recuperar o Nome Anterior do paciente.
	 * 
	 * @param codigoPaciente
	 * @return Nome Anterior
	 */
	public String obterNomeAnteriorPaciente(Integer codigoPaciente) {
		List<AipPacientesJn> list = this.getAipPacientesJnDAO().listaPacientesJn(codigoPaciente);
		if (!list.isEmpty()) {
			return list.get(0).getNomePaciente();
		}
		return null;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected AipPacientesJnDAO getAipPacientesJnDAO(){
		return aipPacientesJnDAO;
	}
	
	protected AipPacientesDAO getAipPacientesDAO() {
		return aipPacientesDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
