package br.gov.mec.aghu.paciente.historico.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTipoProntuario;
import br.gov.mec.aghu.model.AipEndPacientesHist;
import br.gov.mec.aghu.model.AipPacientesHist;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
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
@Stateless
public class HistoricoPacienteRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(HistoricoPacienteRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	private static final long serialVersionUID = 5075026878998169451L;
	

	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;

	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private HistoricoPacienteJournalON historicoPacienteJournalON;

	@EJB
	private HistoricoEnderecoPacienteRN historicoEnderecoPacienteRN;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private enum HistoricoPacienteRNExceptionCode implements
		BusinessExceptionCode {
		AIP_PACIENTES_ERRO_EXCLUIR, AIP_PACIENTES_ERRO_ATUALIZAR, 
		AIP_PACIENTES_ERRO_INSERIR, AIP_PACIENTES_DADOS_INVALIDOS
	}

	/**
	 * Método que chama as implementações das triggers responsáveis pela
	 * inserção de um registro da tabela AIP_PACIENTES_HIST
	 * 
	 * As triggers para INSERT (AIPT_PAH_ARI, AIPT_PAH_ASI, AIPT_PAH_BSI) na
	 * tabela AIP_PACINTES_HIST não implementam regras de negócio.
	 * 
	 * @param pacienteHist
	 */
	public void inserirPacienteHist(AipPacientesHist pacienteHist)
			throws ApplicationBusinessException {
		try {
			this.getPacienteFacade().inserirPacienteHist(pacienteHist, false);
		} catch (ConstraintViolationException ise) {
			StringBuffer valores = new StringBuffer();
		//	InvalidValue[] arr = ise.getInvalidValues();
			for (ConstraintViolation<?> valor : ise.getConstraintViolations()) {
				if (valores.length() == 0) {
					valores.append(valor.getPropertyPath() );
				} else {
					valores.append(", ").append(valor.getPropertyPath());
				}
			}

			throw new ApplicationBusinessException(
					HistoricoPacienteRNExceptionCode.AIP_PACIENTES_DADOS_INVALIDOS,
					valores.toString());

		} catch (Exception e) {
			LOG.error("Erro inserir histórico de paciente.", e);
			throw new ApplicationBusinessException(HistoricoPacienteRNExceptionCode.AIP_PACIENTES_ERRO_ATUALIZAR);
		}
	}

	/**
	 * Método que chama as implementações das triggers responsáveis pela
	 * atualização de um registro da tabela AIP_PACIENTES_HIST. Gerado journal e
	 * executada toda lógica das regras de negócio quando o prntAtivo é alterado
	 * para E ou H (recupera AipPacientes e AipEnderecosPacientes). A trigger
	 * para UPDATE AIPT_PAH_ARU não implementa regras de negócio.
	 * 
	 * @param pacienteHist
	 * @throws ApplicationBusinessException
	 */
	public void atualizarPacienteHist(AipPacientesHist pacienteHist) throws ApplicationBusinessException {
		try {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			Boolean executou = this.verificarSituacaoPacienteHistoricoUpdate(pacienteHist, servidorLogado != null ? servidorLogado.getUsuario() : null);
			
			// Caso o método "verificarSituacaoPacienteHistoricoUpdate()" tenha
			// sido executado, esse já terá persistido o pacienteHist.
			if (!executou) {
				this.getPacienteFacade().atualizarPacienteHist(pacienteHist, true);
			}
		} catch (Exception e) {
			logError("Erro ao atualizar o histórico do paciente.", e);
			throw new ApplicationBusinessException(
					HistoricoPacienteRNExceptionCode.AIP_PACIENTES_ERRO_ATUALIZAR);
		}
	}

	/**
	 * Método que chama as implementações das triggers responsáveis pela remoção
	 * de um registro da tabela AIP_PACIENTES_HIST nas operações de DELETE.
	 * 
	 * @param pacienteHist
	 * @throws ApplicationBusinessException
	 */
	public void removerPacienteHist(AipPacientesHist pacienteHist)
			throws ApplicationBusinessException {
		if(pacienteHist != null){
			try {
				IPacienteFacade pacienteFacade = this.getPacienteFacade();
				
				pacienteFacade.removerPacienteHist(pacienteHist, false);
				
				gerarJournalPacienteHist(null, pacienteHist,
						DominioOperacoesJournal.DEL);
				
				this.flush();
			} catch (Exception e) {
				LOG.error("Erro ao remover o histórico do paciente.", e);
				throw new ApplicationBusinessException(
						HistoricoPacienteRNExceptionCode.AIP_PACIENTES_ERRO_EXCLUIR);
			}
		}
	}

	
	/**
	 * Método que verifica se a situação atual do histórico (prntAtivo) é
	 * diferente da situação anterior e se a mesma é igual a H ou E. Caso seja,
	 * cria paciente e endereço de paciente com base no registro de histórico do
	 * paciente.
	 * 
	 * ORADB Trigger AIPT_PAH_ASU
	 * 
	 * @param pacienteHist
	 * @throws ApplicationBusinessException
	 */
	public Boolean verificarSituacaoPacienteHistoricoUpdate(AipPacientesHist pacienteHist, String usuarioLogado) throws ApplicationBusinessException {
		return this.verificarSituacaoPacienteHistorico(pacienteHist, usuarioLogado);
	}

	/**
	 * Método que implementa a trigger para geração de journal de DELETE nos
	 * objetos AipPacinetesHist.
	 * 
	 * ORADB Trigger AIPT_PAH_ARD
	 * 
	 * @param pacienteHist
	 * @throws ApplicationBusinessException
	 */
	private void gerarJournalPacienteHist(AipPacientesHist pacienteHist,
			AipPacientesHist pacienteHistAnterior,
			DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		this.getHistoricoPacienteJournalON().observarPersistenciaHistoricoPaciente(
				pacienteHist, pacienteHistAnterior, operacao);
	}
	
	/**
	 * Método que implementa regras da procedure AIPP_ENFORCE_PAH_RULES. Esse
	 * método verifica se a situação atual do histórico (prntAtivo) é diferente
	 * da situação anterior e se a mesma é igual a H ou E. Caso seja, cria
	 * paciente e endereço de paciente com base no registro de histórico do
	 * paciente.
	 * 
	 * ORADB Procedure AIPP_ENFORCE_PAH_RULES
	 * 
	 * @param pacienteHist
	 * @throws AGHUNegocioException
	 */
	public Boolean verificarSituacaoPacienteHistorico(AipPacientesHist pacienteHist, String usuarioLogado)
			throws ApplicationBusinessException {

		Boolean ret = false;
		
		// Busca pacienteHist anterior
		AipPacientesHist pacienteHistAnterior = obterPacienteHistAnterior(pacienteHist
				.getCodigo());

		// Verifica se situação atual do paciente é diferente da situação
		// anterior. Caso seja, verifica se situação atual é diferente de H e E
		// e se a situação anterior é igual a H ou E.
		if (pacienteHist.getPrntAtivo() != pacienteHistAnterior.getPrntAtivo()
				&& !DominioTipoProntuario.E.equals(pacienteHist.getPrntAtivo())
				&& !DominioTipoProntuario.H.equals(pacienteHist.getPrntAtivo())
				&& (DominioTipoProntuario.E.equals(pacienteHistAnterior
						.getPrntAtivo()) || DominioTipoProntuario.H
						.equals(pacienteHistAnterior.getPrntAtivo()))) {
			ret = true;

			this.gerarJournalPacienteHist(pacienteHist, pacienteHistAnterior,
					DominioOperacoesJournal.UPD);
			
			this.recuperarPacienteDeHistorico(pacienteHist);
		}
		
		return ret;
	}

	/**
	 * Método para criar um objeto AipPacientes com base no AipPacientesHist e
	 * criar os objetos AipEnderecosPacientes através dos objetos
	 * AipEndPacientesHist. Após a criação dos objetos AipPacientes e
	 * AipEnderecosPacientes, os objetos AipPacientesHist e AipEndPacientesHist
	 * são excluídos.
	 * 
	 * ORADB Procedure AIPK_PAC_ATU.RN_PACP_EXC_HISTORIC
	 * 
	 * @param pacienteHist
	 * @throws ApplicationBusinessException
	 */
	private void recuperarPacienteDeHistorico(AipPacientesHist pacienteHist)
			throws ApplicationBusinessException {
		
		// Cria um paciente com base no seu registro de histórico
		this.getCadastroPacienteFacade().persistirPacienteDeHistoricoPaciente(pacienteHist);
		
		// Remove endereços do histórico do paciente (AipEndPacientesHist)
		List<AipEndPacientesHist> endPacList = this.getHistoricoEnderecoPacienteRN()
				.pesquisarHistoricoEnderecoPaciente(pacienteHist.getCodigo());
		if (endPacList != null) {
			for (AipEndPacientesHist endHistorico : endPacList) {
				this.getHistoricoEnderecoPacienteRN().removerEnderecoPacienteHist(endHistorico);
			}
		}

		// Remove histórico do paciente (AipPacientesHist)
		this.removerPacienteHist(pacienteHist);
	} 

	/**
	 * Método para furar o cache de 1º nível do hibernate e buscar os valores
	 * anteriores de AipPacientesHist.
	 * 
	 * @param codigo
	 *            do paciente
	 * @return AipPacientesHist
	 */
	private AipPacientesHist obterPacienteHistAnterior(Integer codigo) {
		Object[] pacienteHist = this.getPacienteFacade().obterPacienteHistAnterior(codigo);
		
		AipPacientesHist pacienteHistRetorno = null;

		if (pacienteHist != null) {
			pacienteHistRetorno = new AipPacientesHist();
			pacienteHistRetorno.setCodigo((Integer) pacienteHist[0]);
			pacienteHistRetorno.setNome((String) pacienteHist[1]);
			pacienteHistRetorno
					.setPrntAtivo((DominioTipoProntuario) pacienteHist[2]);
			pacienteHistRetorno.setProntuario((Integer) pacienteHist[3]);
		}

		return pacienteHistRetorno;
	}
	
	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return (ICadastroPacienteFacade) cadastroPacienteFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return (IPacienteFacade) pacienteFacade;
	}
	
	protected HistoricoPacienteJournalON getHistoricoPacienteJournalON() {
		return historicoPacienteJournalON;
	}

	protected HistoricoEnderecoPacienteRN getHistoricoEnderecoPacienteRN() {
		return historicoEnderecoPacienteRN;
	}
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

}
