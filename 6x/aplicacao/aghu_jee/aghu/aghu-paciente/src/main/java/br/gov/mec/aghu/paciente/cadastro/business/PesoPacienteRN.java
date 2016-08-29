package br.gov.mec.aghu.paciente.cadastro.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.dominio.DominioMomento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AipPesoPacientes;
import br.gov.mec.aghu.model.MciFatorPredisponentes;
import br.gov.mec.aghu.model.MciMvtoFatorPredisponentes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPesoPacientesDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
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
public class PesoPacienteRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PesoPacienteRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	private AipPesoPacientesDAO aipPesoPacientesDAO;
	
	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@EJB
	private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 1615795233237230919L;

	private enum PesoPacienteRNExceptionCode implements BusinessExceptionCode {
		PESO_RN_JA_CADASTRADO, AIP_00191, AIP_00286;
	}

	/**
	 * Método usado para validar se um paciente recém nascidos não possui um
	 * peso registrado anteriormente no sistema.
	 * 
	 * Implementa a procedure AIPP_ENFORCE_PEP_RULES e
	 * AIPK_PEP_RN.RN_PEPP_VER_PESO_RN
	 * 
	 * Este método não é usado hoje no sistema. Será chamado posteriormente na
	 * inclusão de pacientes, quando houver a possibilidade de haver mais de um
	 * peso p/ um paciente.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void validarPesoDuplicadoRecemNascido(AipPesoPacientes pesoPaciente)
			throws ApplicationBusinessException {

		if (pesoPaciente.getPeso() != null
				&& pesoPaciente.getIndMomento() == DominioMomento.N) {

			List<AipPesoPacientes> listaPesos = this.getAipPesoPacientesDAO()
					.listarPesosPacientesPorCodigoPaciente(
							pesoPaciente.getAipPaciente().getCodigo());
			for (AipPesoPacientes peso : listaPesos) {

				if (peso != pesoPaciente
						&& peso.getIndMomento() == DominioMomento.N) {
					throw new ApplicationBusinessException(
							PesoPacienteRNExceptionCode.PESO_RN_JA_CADASTRADO);

				}

			}

		}

	}
	
	/**
	 * ORADB Código equivalente as triggers AIPT_PEP_ARI, AIPT_PEP_ASI, AIPT_PEP_BRI, AIPT_PEP_BSI da tabela AIP_PESO_PACIENTES.
	 * Obs.: Faz o mesmo que a procedure AIPP_ENFORCE_PEP_RULES.
	 * @param aipPesoPacientes
	 * @param servidorLogado 
	 * @throws ApplicationBusinessException
	 */
	public void persistirAipPesoPaciente(AipPesoPacientes aipPesoPacientes, RapServidores servidorLogado) throws ApplicationBusinessException {
		validarPesoDuplicadoRecemNascido(aipPesoPacientes);
		salvarPesoPaciente(aipPesoPacientes, servidorLogado);
		
		this.getAipPesoPacientesDAO().persistir(aipPesoPacientes);
	}
	
	/**
	 * Método que realiza as ações da trigger
	 * 
	 * ORADB Trigger AIPT_PEP_BRI.
	 * 
	 * @param peso
	 *            , pessoa
	 * @param servidorLogado2 
	 * @return
	 */
	public void salvarPesoPaciente(AipPesoPacientes peso, RapServidores servidorLogado) throws ApplicationBusinessException {

		if (peso.getId().getCriadoEm() == null) {
			peso.getId().setCriadoEm(new Date());
		}
		
		verificarServidorUsuario(servidorLogado);
		
		if (peso.getRapServidor() == null) {
			peso.setRapServidor(servidorLogado);
		}
		
		if (peso.getIndMomento() != null && peso.getIndMomento().equals(DominioMomento.N)) {
		
			Short vUnfSeq = obterSeqUnidadeFuncionalPaciente(peso.getAipPaciente().getCodigo());
			
			if (existeCaracteristicaUnidadeFuncionalPaciente(vUnfSeq, ConstanteAghCaractUnidFuncionais.UNID_NEONATOLOGIA)) {
				gravarMovimentoFatorPredisponentes(peso.getAipPaciente().getCodigo(), peso.getPeso());
			}
		}
	}
	
	/**
	 * Método que realiza as ações da procedure
	 * 
	 * ORADB Procedure AIPK_PEP_RN.RN_PEPP_GRAVA_MFP.
	 */
	@SuppressWarnings("deprecation")
	public void gravarMovimentoFatorPredisponentes(Integer codigoPaciente, BigDecimal valorPeso) throws ApplicationBusinessException {
		final DominioPacAtendimento indPacAtendimento = DominioPacAtendimento.S;

		MciMvtoFatorPredisponentes mvtoFator = obterMciMvtoFatorPredisponentesPorPaciente(codigoPaciente);
		if (mvtoFator != null) {
			removerMvtosFatorPredisponentes(mvtoFator);
		}
		MciFatorPredisponentes fator = obterFatorPredisponentesPorPeso(valorPeso);
		if (fator != null) {
			
			List<AghAtendimentos> atendimentos = this.getAghuFacade().obterAtendimentosPorPaciente(codigoPaciente,null, indPacAtendimento, ConstanteAghCaractUnidFuncionais.UNID_UTIN);
			
			AghAtendimentos atendimento = !atendimentos.isEmpty() ? atendimentos.get(0) : null;
			
			if (atendimento != null) {
				if (mvtoFator != null && mvtoFator.getAtendimento() != null
						&& mvtoFator.getAtendimento().equals(atendimento)) {
					MciMvtoFatorPredisponentes mvtoFatorInserir = new MciMvtoFatorPredisponentes();
					mvtoFatorInserir.setAtendimento(mvtoFator.getAtendimento());
					mvtoFatorInserir.setPaciente(mvtoFator.getPaciente());
					mvtoFatorInserir.setFatorPredisponente(mvtoFator.getFatorPredisponente());
					mvtoFatorInserir.setCriadoEm(new Date());
					mvtoFatorInserir.setSerMatricula(mvtoFator
							.getSerMatricula());
					mvtoFatorInserir.setSerVinCodigo(mvtoFator
							.getSerVinCodigo());
					mvtoFatorInserir.setDataInicio(mvtoFator.getDataInicio());
					mvtoFatorInserir.setUnfSeq(mvtoFator.getUnfSeq());
					mvtoFatorInserir.setUnfSeqNotificado(mvtoFator
							.getUnfSeqNotificado());
					mvtoFatorInserir.setQrtNumero(mvtoFator.getQrtNumero());
					mvtoFatorInserir.setQrtNumeroNotificado(mvtoFator
							.getQrtNumeroNotificado());
					mvtoFatorInserir.setLtoLtoId(mvtoFator.getLtoLtoId());
					mvtoFatorInserir.setLtoLtoIdNotificado(mvtoFator
							.getLtoLtoIdNotificado());
					mvtoFatorInserir.setDataFim(mvtoFator.getDataFim());
					mvtoFatorInserir.setSerMatriculaEncerrado(mvtoFator
							.getSerMatriculaEncerrado());
					mvtoFatorInserir.setSerVinCodigoEncerrado(mvtoFator
							.getSerVinCodigoEncerrado());
					persistirMvtoFatorPredisponentes(mvtoFatorInserir);
				} else {
					MciMvtoFatorPredisponentes mvtoFatorInserir = new MciMvtoFatorPredisponentes();
					mvtoFatorInserir.setAtendimento(atendimento);
					mvtoFatorInserir.setPaciente(atendimento.getPaciente());
					mvtoFatorInserir.setFatorPredisponente(fator);
					mvtoFatorInserir.setCriadoEm(new Date());
					mvtoFatorInserir.setSerMatricula(atendimento.getServidor()
							.getId().getMatricula());
					mvtoFatorInserir.setSerVinCodigo(atendimento.getServidor()
							.getId().getVinCodigo());
					mvtoFatorInserir.setDataInicio(atendimento.getDthrInicio());
					mvtoFatorInserir.setUnfSeq(atendimento
							.getUnidadeFuncional().getSeq());
					mvtoFatorInserir.setUnfSeqNotificado(atendimento
							.getUnidadeFuncional().getSeq());
					mvtoFatorInserir
							.setQrtNumero(atendimento.getQuarto() == null ? null
									: atendimento.getQuarto().getNumero());
					mvtoFatorInserir.setQrtNumeroNotificado(atendimento
							.getQuarto() == null ? null : atendimento
							.getQuarto().getNumero());
					mvtoFatorInserir.setLtoLtoId(atendimento.getLeito()
							.getLeitoID());
					mvtoFatorInserir.setLtoLtoIdNotificado(atendimento
							.getLeito().getLeitoID());
					mvtoFatorInserir.setDataFim(null);
					mvtoFatorInserir.setSerMatriculaEncerrado(null);
					mvtoFatorInserir.setSerVinCodigoEncerrado(null);
					persistirMvtoFatorPredisponentes(mvtoFatorInserir);
				}
			}
		}
	}
	
	/**
	 * Método que obtem o fator predisponente pelo peso do paciente Método
	 * responsável por implementar a ORADB FUNCTION MCIC_BUSCA_FPD_SEQ
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public MciFatorPredisponentes obterFatorPredisponentesPorPeso(
			BigDecimal valorPeso) {
		return this.getControlerInfeccaoFacade()
				.obterFatorPredisponentesPorPeso(valorPeso);
	}
	
	
	/**
	 * Método que obtem um atendimento pelo paciente, indPacAtendimento e
	 * característica de Unidade Funcional
	 * 
	 * @param codigoPaciente
	 *            , indPacAtendimento, caracteristica
	 * @return
	 */
	public AghAtendimentos obterAtendimentoPorPaciente(Integer codigoPaciente,
			DominioPacAtendimento indPacAtendimento, String caracteristica) {
		return this.getAghuFacade().obterAtendimentoPorPaciente(
				codigoPaciente, indPacAtendimento, caracteristica);
	}
	
	/**
	 * Método que encaminha para a execução de todas as regras de negócio que
	 * envolvem uma inclusão de um Movimento Fator Predisponentes e seguido de
	 * sua persistência no banco.
	 * 
	 * @param listaMvtos
	 * @return
	 */
	public void persistirMvtoFatorPredisponentes(
			MciMvtoFatorPredisponentes mvtoFator) {
		this.getInternacaoFacade().inserirMvtoFatorPredisponente(mvtoFator);
	}

	/**
	 * Método que remove do banco um conjunto de Movimentos Fator Predisponentes
	 * 
	 * @param listaMvtos
	 * @return
	 */
	public void removerMvtosFatorPredisponentes(
			MciMvtoFatorPredisponentes mvtoFator) throws ApplicationBusinessException {
		// Este método deverá chamar a implementação das Triggers e Enforces
		// responsáveis pela remoção de um
		// registro da tabela MCI_FATOR_PREDISPONENTES
		this.getInternacaoFacade().removerMvtoFatorPredisponente(mvtoFator);
	}
	
	
	/**
	 * Método que obtem uma lista de Movimentos Fator Predisponentes pelo código
	 * do paciente
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public MciMvtoFatorPredisponentes obterMciMvtoFatorPredisponentesPorPaciente(
			Integer codigoPaciente) {
		return this.getControlerInfeccaoFacade()
				.obterMciMvtoFatorPredisponentesPorPaciente(codigoPaciente);
	}
	
	/**
	 * Método que verifica se uma dada característica de Unidade Funcional
	 * existe
	 * 
	 * @param unfSeq
	 *            , caracteristica
	 * @return
	 */
	public boolean existeCaracteristicaUnidadeFuncionalPaciente(Short unfSeq,
			ConstanteAghCaractUnidFuncionais caracteristica) {
		List<AghCaractUnidFuncionais> listaCaracteristicas = this
				.getAghuFacade()
				.listaCaracteristicasUnidadesFuncionaisPaciente(unfSeq,
						caracteristica);

		if (listaCaracteristicas.size() == 0) {
			return false;
		}

		return true;
	}
	
	/**
	 * Método que obtem o id da Unidade Funcional do paciente salvo no banco
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	public Short obterSeqUnidadeFuncionalPaciente(Integer codigoPaciente) {
		return this.getAipPacientesDAO().obterSeqUnidadeFuncionalPaciente(
				codigoPaciente);
	}
	
	/**
	 * Método que verifica se o usuário logado está cadastrado como servidor.
	 */
	private void verificarServidorUsuario(final RapServidores servidorLogado) throws ApplicationBusinessException {
		if (servidorLogado == null || servidorLogado.getId() == null || servidorLogado.getId().getMatricula() == null) {
			throw new ApplicationBusinessException(PesoPacienteRNExceptionCode.AIP_00286);
		}
	}

	protected AipPesoPacientesDAO getAipPesoPacientesDAO() {
		return aipPesoPacientesDAO;
	}
	
	protected AipPacientesDAO getAipPacientesDAO(){
		return aipPacientesDAO;
	}

	
	protected IControleInfeccaoFacade getControlerInfeccaoFacade() {
		return this.controleInfeccaoFacade;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
