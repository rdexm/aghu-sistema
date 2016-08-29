package br.gov.mec.aghu.ambulatorio.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.MamDiagnosticoDAO;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioIndPendenteDiagnosticos;
import br.gov.mec.aghu.dominio.DominioSexoDeterminante;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.MamDiagnostico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe para implementação de regras de negócio para Listagem de Diagnósticos
 * Ativos para Pacientes (dentro do pacote {@code prescricao}).
 * 
 * @author ptneto
 *
 */
@Stateless
public class ListarDiagnosticosAtivosPacienteRN extends BaseBusiness{
	
	private static final String INSERIDO = "inserido";

	private static final Log LOG = LogFactory.getLog(ListarDiagnosticosAtivosPacienteRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MamDiagnosticoDAO mamDiagnosticoDAO;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6749485910579316194L;

	/**
	 * Enumeração para mensagens de excpetion.
	 * @author ptneto
	 *
	 */
	public enum DiagnosticosAtivosPacienteRNExceptionCode implements BusinessExceptionCode {
		
		/**
		 * RN01 - código de paciente não definido.
		 */
		MENSAGEM_COD_PACIENTE_INDEFINIDO,			
		/**
		 * RN04 - Quando a data de início do atendimento for menor que a data de nascimento ( - 270 dias)
		 */
		MENSAGEM_DATA_INICIO_MENOR_QUE_NASCIMENTO,
		/**
		 * RN04 - Quando a data de início for maior que data atual.  
		 */
		MENSAGEM_DATA_INICIO_MAIOR_QUE_ATUAL,
		/**
		 * RN04 - Quando data de nascimento é nula.
		 */				
		MENSAGEM_DATA_NASCIMENTO_NULA,
		/**
		 * RN05 - Quando a data de fim for menor que a data de início do atendimento.
		 */
		MENSAGEM_DATA_FIM_MENOR_QUE_INICIO,
		/**
		 * RN14 - Exclusão feita para diganóstico já validado.
		 */
		MENSAGEM_EXCLUSAO_DIAG_VALIDADO, MENSAGEM_CID_SEXO_INCOMPATIVEL,
		MAM_00647
	}

	

	
	/**
	 * Insere um novo registro de diagnóstico.
	 * @param diagnostico Registro a ser inserido.
	 * @param categoriaProfissional Categoria do profissional logado.
	 * @throws ApplicationBusinessException
	 */
	public void insert (MamDiagnostico diagnostico, CseCategoriaProfissional categoriaProfissional) 
		throws ApplicationBusinessException{
				
		this.beforeRowInsert(diagnostico, categoriaProfissional);
	}

	/**
	 * 
	 * @param diagnostico
	 * @throws ApplicationBusinessException
	 */
	public MamDiagnostico inserir(MamDiagnostico diagnostico) throws ApplicationBusinessException {
		List<CseCategoriaProfissional> lista = getCascaFacade().pesquisarCategoriaProfissional(diagnostico.getServidor());
		CseCategoriaProfissional categoria =  null;
		if(lista!=null && lista.size()>0){
			categoria = lista.get(0);
		}
		this.beforeRowInsert(diagnostico, categoria);
		this.getDiagnosticoDAO().persistir(diagnostico);
		this.getDiagnosticoDAO().flush();
		return diagnostico;
		
	}

	/**
	 * @param diagnostico
	 * @param diagnosticoOld
	 * @throws BaseException
	 */
	public void atualizarDiagnostico(MamDiagnostico diagnostico, MamDiagnostico diagnosticoOld) throws BaseException {
		this.validarAtualizacaoDiagnostico(diagnostico, diagnosticoOld);
	}
	
	/**
	 * Atualiza um registro de diagnóstico.
	 * @param diagnostico
	 * @throws ApplicationBusinessException
	 */
	public void update (MamDiagnostico diagnostico, RapServidores servidor, boolean dtIncioAlterada,
			boolean dtFimAlterada, boolean cidAlterado) throws ApplicationBusinessException {
		
		this.beforeRowUpdate(diagnostico, servidor, dtIncioAlterada, dtFimAlterada, cidAlterado);
	}
	/**
	 * Remove um registro de diagnóstico.
	 * @param diagnostico
	 * @throws ApplicationBusinessException
	 */
	public void delete (MamDiagnostico diagnostico) throws ApplicationBusinessException {
		
		this.beforeRowDelete(diagnostico);
	}
	
	/**
	 * Trigger
	 * ORADB MAMT_DIA_BRI 
	 * @param diagnostico
	 * @param categoriaProfissional
	 * @throws ApplicationBusinessException
	 */
	public void beforeRowInsert (MamDiagnostico diagnostico, CseCategoriaProfissional categoriaProfissional) throws ApplicationBusinessException {
		
		
		IPrescricaoMedicaFacade prescricaoMedicaFacade = this.getPrescricaoMedicaFacade();

		// RN01
		this.verificaCodigoPaciente(diagnostico);
		if (diagnostico.getCid()!=null){
			// RN02
			prescricaoMedicaFacade.verificarStatusCid(diagnostico.getCid());
			// RN03
			verificarSexoCompativel(diagnostico.getCid(), diagnostico.getPaciente());
		}
		// RN04 (as duas chamadas abaixo)
		this.verificaDataInicio(diagnostico);
		this.verificaDataNascimento(diagnostico);
		// RN05
		this.verificaDataFim(diagnostico);
		// RN06
		// manterDiagnosticoAtendimentoRN.popularServidor
		// (diagnostico.getCidAtendimento(), diagnostico.getServidor());
		// RN08
		diagnostico.setCategoriaProfissional(categoriaProfissional);
	}
	/**
	 * Triggers de atualização de registro.
	 * @param diagnostico
	 * @throws ApplicationBusinessException
	 */
	public void beforeRowUpdate (MamDiagnostico diagnostico, RapServidores servidor, boolean dtInicioAlterada,
			boolean dtFimAlterada, boolean cidAlterado)  throws ApplicationBusinessException {
		
		
		IPrescricaoMedicaFacade prescricaoMedicaFacade = this.getPrescricaoMedicaFacade();
			
			// RN09
//			this.validarDiagnostico(diagnostico);
			
			// RN10
			if (diagnostico.getIndSituacao() == DominioSituacao.I) {
				diagnostico.setServidorMovimento (servidor);
				diagnostico.setDthrMvto (Calendar.getInstance().getTime());
			}
	
			// RN11 - ON deve ter método para validar se data de início foi alterada.
			if (dtInicioAlterada) {
				// se alteração foi feita, segue as mesmas normas da regra RN04.
				this.verificaDataInicio (diagnostico);
				this.verificaDataNascimento (diagnostico);
			}			
			
			// RN12
			if (dtFimAlterada) {
				// se a alteração foi feita, segue as mesmas normas da regra RN05
				this.verificaDataFim (diagnostico);
			}
			
			// RN13
			if (cidAlterado) {
				// se CID foi alterado, segue as mesmas normas das regras RN02 e RN03
				prescricaoMedicaFacade.verificarStatusCid(diagnostico.getCid());
			}
		
	}
	
	/**
	 * Triggers de atualização do diagnostico.
	 * ORADB 
	 * Trigger MAMT_DIA_BRU
	 * @param diagnostico
	 * @throws ApplicationBusinessException
	 */
	public void validarAtualizacaoDiagnostico (MamDiagnostico diagnostico, MamDiagnostico diagnosticoOld)  throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(diagnosticoOld.getIndPendente().equals(DominioIndPendenteDiagnosticos.V)){
			if((diagnostico.getCid()!=null && !diagnostico.getCid().equals(diagnosticoOld.getCid())) 
					|| !diagnostico.getData().equals(diagnosticoOld.getData()) 
					|| (diagnostico.getDescricao()!=null && !diagnostico.getDescricao().equals(diagnosticoOld.getDescricao()))){
				throw new ApplicationBusinessException(DiagnosticosAtivosPacienteRNExceptionCode.MAM_00647);
			}
		}
		if(!diagnostico.getIndSituacao().equals(diagnosticoOld.getIndSituacao()) && diagnostico.getIndSituacao().equals(DominioSituacao.I)){
			diagnostico.setServidor(servidorLogado);
			diagnostico.setDthrMvto(new Date());
		}
	
		if(!diagnostico.getData().equals(diagnosticoOld.getData())){
			this.verificaDataInicio (diagnostico);
			this.verificaDataNascimento (diagnostico);
		}

		if(diagnostico.getDataFim() != null && !diagnostico.getDataFim().equals(diagnosticoOld.getData())){
				this.verificaDataFim (diagnostico);
		}
		
		if(diagnostico.getCid()!=null && !diagnostico.getCid().equals(diagnosticoOld.getCid())){
			this.getPrescricaoMedicaFacade().verificarStatusCid(diagnostico.getCid());
		}
		
	}
	/**
	 * Triggers de remoção de registro.
	 * @param diagnostico
	 * @throws ApplicationBusinessException
	 */
	private void beforeRowDelete (MamDiagnostico diagnostico) throws ApplicationBusinessException {		

		this.validarDiagnostico(diagnostico);

	}
	/**
	 * Se o código do paciente não foi informado, dispara a exception.
	 * @param diagnostico
	 * @throws CodigoPacienteIndefinidoException
	 * @throws ApplicationBusinessException
	 */
	private void verificaCodigoPaciente (MamDiagnostico diagnostico)
		throws  ApplicationBusinessException {
		
		if ((diagnostico == null) || (diagnostico.getPaciente().getCodigo() == null)) {
			throw new ApplicationBusinessException(DiagnosticosAtivosPacienteRNExceptionCode.MENSAGEM_COD_PACIENTE_INDEFINIDO,
			INSERIDO);
		}
	}
	/**
	 * Faz consistência do campo data de início do atendimento.
	 * @param diagnostico
	 * @throws DataInicioMenorQueNascimentoException
	 * @throws DataIncioMaiorQueAtualException
	 * @throws ApplicationBusinessException
	 */
	private void verificaDataInicio (MamDiagnostico diagnostico) 
		throws  ApplicationBusinessException {
		
		// se a data de início for menor que a data de nascimento do paciente - 270 dias.
		Calendar cal = Calendar.getInstance();
		cal.setTime(diagnostico.getPaciente().getDtNascimento());
		long nascimentoMilis = cal.getTimeInMillis();
		long gravidezMilis = nascimentoMilis - 23328000000L; //valor equivalente a 270 dias em milisegundos
		if (diagnostico.getData().before(new Date(gravidezMilis))) {
			throw new ApplicationBusinessException(DiagnosticosAtivosPacienteRNExceptionCode.MENSAGEM_DATA_INICIO_MENOR_QUE_NASCIMENTO,
			INSERIDO);
		}
		
		// se a data de início for maior que a data atual do sistema.
		if (diagnostico.getData().after(Calendar.getInstance().getTime())) {
			throw new ApplicationBusinessException(DiagnosticosAtivosPacienteRNExceptionCode.MENSAGEM_DATA_INICIO_MAIOR_QUE_ATUAL,
			INSERIDO);
		}			
	}
	/**
	 * Verifica se a data de nascimento do paciente está nula.
	 * @param diagnostico
	 * @throws DataNascimentoNulaException
	 * @throws ApplicationBusinessException
	 */
	private void verificaDataNascimento (MamDiagnostico diagnostico) 
		throws ApplicationBusinessException {
		
		if (diagnostico.getPaciente().getDtNascimento() == null) {
			throw new ApplicationBusinessException(DiagnosticosAtivosPacienteRNExceptionCode.MENSAGEM_DATA_NASCIMENTO_NULA, 
			INSERIDO);
		}
	}
	/**
	 * Faz consistência do campo data fim do atendimento.
	 * @param diagnostico
	 * @throws DataFimMenorQueInicioException
	 * @throws DataFimMaiorQueAtualException
	 * @throws ApplicationBusinessException
	 */
	private void verificaDataFim (MamDiagnostico diagnostico) 
		throws ApplicationBusinessException {
		
		if (diagnostico.getDataFim() != null) {
			// se a data de fim for menor do que a data de início.
			if (diagnostico.getDataFim().before(diagnostico.getData())) {
				throw new ApplicationBusinessException(DiagnosticosAtivosPacienteRNExceptionCode.MENSAGEM_DATA_FIM_MENOR_QUE_INICIO, 
				INSERIDO);
			}
			// se a data de fim for maior do que a data atual do sistema.
			if (diagnostico.getDataFim().after(Calendar.getInstance().getTime())) {
				throw new ApplicationBusinessException(DiagnosticosAtivosPacienteRNExceptionCode.MENSAGEM_DATA_FIM_MENOR_QUE_INICIO, 
				INSERIDO);
			}
		}
	}
	/**
	 * Verifica se um diagnóstico já está validado.
	 * @param diagnostico
	 * @throws DiagnosticoValidadoException
	 * @throws ApplicationBusinessException
	 */
	private void validarDiagnostico (MamDiagnostico diagnostico) 
		throws ApplicationBusinessException {
		
		if (DominioIndPendenteDiagnosticos.V.equals(diagnostico.getIndPendente())) {
			throw new ApplicationBusinessException(DiagnosticosAtivosPacienteRNExceptionCode.MENSAGEM_EXCLUSAO_DIAG_VALIDADO, 
			"excluído");
		}
	}	
	
	
	protected void verificarSexoCompativel(AghCid cid,
			AipPacientes paciente) throws ApplicationBusinessException {

		// se o campo "restrição" for "Q" não faz comparação de sexo entre
		// paciente e CID.
		if (cid.getRestricaoSexo()
				.equals(DominioSexoDeterminante.Q)) {
			return;
		}

		// obtem o paciente do atendimento.
		if ((paciente.getSexo() != null)
				&& (! StringUtils.equalsIgnoreCase(paciente.getSexo().getDescricao(), cid.getRestricaoSexo().getDescricao()))) {
			throw new ApplicationBusinessException(DiagnosticosAtivosPacienteRNExceptionCode.MENSAGEM_CID_SEXO_INCOMPATIVEL,
			INSERIDO);
		}
	}
	
	private MamDiagnosticoDAO getDiagnosticoDAO() {
		return mamDiagnosticoDAO;
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}
	
	protected ICascaFacade getCascaFacade() {
		return cascaFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
