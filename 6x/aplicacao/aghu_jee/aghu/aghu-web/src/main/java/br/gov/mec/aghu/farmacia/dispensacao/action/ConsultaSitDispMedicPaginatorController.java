package br.gov.mec.aghu.farmacia.dispensacao.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.CodPacienteFoneticaVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.vo.MpmPrescricaoMedicaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public class ConsultaSitDispMedicPaginatorController extends ActionController implements ActionPaginator {


	private static final long serialVersionUID = 6790244134413411730L;
	
	private static final String PRESCRICAO_SITUACAO_DISPENSACAO_LIST = "paciente-pesquisaPacienteComponente";
	
	private AinLeitos leito;
	private Date dtHrInicio;
	private Date dtHrFim;
	private String seqPrescricao;
	private Integer seqPrescricaoInt;
	
	private Integer pacCodigo;
	private Integer prontuario;
	private AipPacientes paciente;
	
	private Boolean indPacAtendimento = Boolean.TRUE; //DominioPacAtendimento
	private Boolean indPmNaoEletronica; 
	private Boolean isLock;	
	
	private String voltarPara;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	//#37108
	private Boolean pesquisaAutomatica = Boolean.FALSE;
	
	@Inject @Paginator
	private DynamicDataModel<MpmPrescricaoMedicaVO> dataModel;
	
	@Inject
	@SelectionQualifier
	private Instance<CodPacienteFoneticaVO> codPacienteFonetica;
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	public void inicio() {
	 


		processaBuscaPaciente();
		if(pesquisaAutomatica){
			dataModel.reiniciarPaginator();
			pesquisaAutomatica = Boolean.FALSE;
		}
	
	}

	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public void pesquisar() {
		dataModel.reiniciarPaginator();			
	}

	/**
	 * Limpa a pesquisa de prescricoes
	 */
	public void limparPesquisa() {
		this.leito = null;
		this.paciente = null;
		this.prontuario = null;
		this.pacCodigo = null;
		this.dtHrInicio = null;
		this.dtHrFim = null;
		this.seqPrescricao = null;
		this.seqPrescricaoInt = null;
		this.indPacAtendimento = Boolean.TRUE;
		this.indPmNaoEletronica = null;
		dataModel.setPesquisaAtiva(false);

	}

	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}

	public String voltarPara() {
		return this.voltarPara;
	}
	
	public List<AinLeitos> pesquisarLeitos(String strPesquisa){
		return this.returnSGWithCount(this.internacaoFacade.obterLeitosAtivosPorUnf(strPesquisa, null),pesquisarLeitosCount(strPesquisa));
	}
	
	public Long pesquisarLeitosCount(String strPesquisa){
		return this.internacaoFacade.obterLeitosAtivosPorUnfCount(strPesquisa, null);
	}

	@Override
	public Long recuperarCount() {
		String leitoId = null;
		String nome = null;
		
		if(leito != null && leito.getLeitoID() != null){
			leitoId = leito.getLeitoID();
		}
	
		if(paciente != null){
			nome = paciente.getNome();
		}
		
		try {
			
			return this.prescricaoMedicaFacade
					.listarPrescricaoMedicaSituacaoDispensacaoCount(leitoId,
							prontuario, nome, dtHrInicio, dtHrFim, seqPrescricao, seqPrescricaoInt, indPacAtendimento, indPmNaoEletronica);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	@Override
	public List<MpmPrescricaoMedicaVO> recuperarListaPaginada(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		
		String leitoId = null;
		String nome = null;
		
		if(leito != null && leito.getLeitoID() != null){
			leitoId = leito.getLeitoID();
		}
		
		if(paciente != null){
			nome = paciente.getNome();
		}

		List<MpmPrescricaoMedicaVO> result = null;
		try {
			result = this.prescricaoMedicaFacade
					.listarPrescricaoMedicaSituacaoDispensacao(firstResult,
							maxResult, orderProperty, asc, leitoId, prontuario,
							nome, dtHrInicio, dtHrFim, seqPrescricao, seqPrescricaoInt, indPacAtendimento, indPmNaoEletronica);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return result;
	}

	//utilizado em action de mec:commandButton de pesquisa fonética
	public String redirecionarPesquisaFonetica(){
		paciente = null;
		return PRESCRICAO_SITUACAO_DISPENSACAO_LIST;
	}
	
	public String redirecionarMedicamentosSituacaoDispensacaoList(){
		return "medicamentosSituacaoDispensacaoList";
	}

	public void processaBuscaPaciente(){
		if (paciente == null || paciente.getCodigo() == null) {
			CodPacienteFoneticaVO codPac = codPacienteFonetica.get();
			if (codPac != null && codPac.getCodigo() > 0) { 
				paciente = pacienteFacade.obterAipPacientesPorChavePrimaria(codPac.getCodigo());
				if (paciente != null) {
					prontuario = paciente.getProntuario();
					pacCodigo = paciente.getCodigo();
				}
			}
		}else if(prontuario != null){
			try {
				paciente = pacienteFacade.obterPacientePorCodigoOuProntuario(prontuario,pacCodigo, null);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
			
	}
	
	public Date getDtHrInicio() {
		return dtHrInicio;
	}

	public void setDtHrInicio(Date dtHrInicio) {
		this.dtHrInicio = dtHrInicio;
	}

	public Date getDtHrFim() {
		return dtHrFim;
	}

	public void setDtHrFim(Date dtHrFim) {
		this.dtHrFim = dtHrFim;
	}

	public String getSeqPrescricao() {
		return seqPrescricao;
	}

	public void setSeqPrescricao(String seqPrescricao) {
		this.seqPrescricao = seqPrescricao;
	}

	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}

	public AinLeitos getLeito() {
		return leito;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public void setInternacaoFacade(IInternacaoFacade internacaoFacade) {
		this.internacaoFacade = internacaoFacade;
	}
	
	public IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}

	public Boolean getIndPacAtendimento() {
		return indPacAtendimento;
	}

	public Boolean getIndPmNaoEletronica() {
		return indPmNaoEletronica;
	}

	public void setIndPacAtendimento(Boolean indPacAtendimento) {
		this.indPacAtendimento = indPacAtendimento;
	}

	public void setIndPmNaoEletronica(Boolean indPmNaoEletronica) {
		this.indPmNaoEletronica = indPmNaoEletronica;
	}

	public Boolean getIsLock() {
		return isLock;
	}

	public void setIsLock(Boolean isLock) {
		this.isLock = isLock;
	}

	public Integer getSeqPrescricaoInt() {
		return seqPrescricaoInt;
	}

	public void setSeqPrescricaoInt(Integer seqPrescricaoInt) {
		this.seqPrescricaoInt = seqPrescricaoInt;
	}

	public Boolean getPesquisaAutomatica() {
		return pesquisaAutomatica;
	}

	public void setPesquisaAutomatica(Boolean pesquisaAutomatica) {
		this.pesquisaAutomatica = pesquisaAutomatica;
	}

	public DynamicDataModel<MpmPrescricaoMedicaVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MpmPrescricaoMedicaVO> dataModel) {
		this.dataModel = dataModel;
	}
}