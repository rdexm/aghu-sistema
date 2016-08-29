package br.gov.mec.aghu.blococirurgico.action;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.paciente.action.PesquisaPacienteController;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;


public class PesquisaCirurgiaRealizadaNotaConsumoPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<MbcCirurgias> dataModel;

	private static final Log LOG = LogFactory.getLog(PesquisaCirurgiaRealizadaNotaConsumoPaginatorController.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4038614901761601452L;
	
	/*
	 * Injeções
	 */
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	/*
	 * Campos do filtro
	 */
	private Integer crgSeq;
	private AghUnidadesFuncionais unidadeCirurgica;
	private Date dataCirurgia;
	private Short numeroAgenda;
	private DominioSimNao indDigtNotaSala;
	private AipPacientes paciente;
	private Integer prontuario;
	private Integer pacCodigoFonetica;
	private String nomePaciente;
	private String voltarPara;
	private boolean iniciaPesquisando;
	private String nomeMicrocomputador;

	
	private final String PAGE_REGISTRO_CIRUGIA_REALIZADA = "registroCirurgiaRealizada";
	private final String PAGE_PESQUISA_REGISTRO_CIRURGIA_REALIZADA = "pesquisaCirurgiaRealizadaNotaConsumo";
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente"; 
	
	
	@Inject
	private RegistroCirurgiaRealizadaController registroCirurgiaRealizadaController;
			
	
	@Inject 
	private PesquisaPacienteController pesquisaPacienteController;
	
	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio() {
	 

	 


		if (unidadeCirurgica == null) {
			try {
				
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					LOG.error("Exceção capturada:", e);
				}
				
				unidadeCirurgica = blocoCirurgicoFacade.obterUnidadeFuncionalCirurgia(nomeMicrocomputador);
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
		
		
		
		if (this.pacCodigoFonetica != null && this.pacCodigoFonetica > 0) { 
			this.setPaciente(this.pacienteFacade.obterPaciente(this.pacCodigoFonetica));
			this.prontuario = this.paciente.getProntuario();
			if(iniciaPesquisando){
				this.pesquisar();
			}
		}
		
	
		// Garante que os resultados da pesquisa serão mantidos ao retonar na tela
		/*if (this.getFirstResult() == 0 && this.ativo) {
			this.pesquisar();
		}*/
	
	}
	

	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Método executado ao clicar no botão pesquisar
	 */
	public String pesquisar() {
//		Melhoria para carregar os dados do paciente no html.
		if(this.prontuario != null && iniciaPesquisando == false){
			this.paciente = this.pacienteFacade.obterPacientePorProntuario(prontuario);
			this.setPacCodigoFonetica(this.paciente.getCodigo());
		} else if (this.pacCodigoFonetica != null && iniciaPesquisando == false){
			this.paciente = this.pacienteFacade.obterPaciente(pacCodigoFonetica);
			this.setProntuario(this.paciente.getProntuario());
		}
        if(this.numeroAgenda != null && dataCirurgia != null && unidadeCirurgica != null){
			List<MbcCirurgias> listRetorno = this.blocoCirurgicoFacade.pesquisarCirurgiasRealizadasNotaConsumo(0, 10, null, false, this.getElemento());
			if(listRetorno != null && listRetorno.size() > 0 ){
				return this.editarRegistroCirurgiaRealizada(listRetorno.get(0).getSeq());				
			}
		}		

		this.dataModel.reiniciarPaginator();
        return null;  
	}
	
	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorUnidadeExecutora(final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutora((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS,  true),listarUnidadesFuncionaisPorUnidadeExecutoraCount(strPesquisa));
	}

	public Long listarUnidadesFuncionaisPorUnidadeExecutoraCount(final String strPesquisa) {
		return this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutoraCount((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, true);
	}
	
	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		// Limpa filtro
		this.crgSeq = null;
		this.unidadeCirurgica = null;
		this.dataCirurgia = null;
		this.numeroAgenda = null;
		this.indDigtNotaSala = null;
		this.paciente = null;
		this.prontuario = null;
		this.pacCodigoFonetica = null;

		this.dataModel.limparPesquisa();
	}

	/**
	 * Obtem o elemento/filtro da pesquisa paginada
	 * 
	 * @return
	 */
	private MbcCirurgias getElemento() {
		MbcCirurgias elemento = new MbcCirurgias();
		elemento.setUnidadeFuncional(this.unidadeCirurgica);
		elemento.setData(this.dataCirurgia);
		elemento.setNumeroAgenda(this.numeroAgenda);
		elemento.setDigitaNotaSala(this.indDigtNotaSala != null ? this.indDigtNotaSala.isSim() : null);
		elemento.setPaciente(this.paciente);
		return elemento;
	}
	
	public String voltarParaCirurgiasEmLote(){
		return voltar();
	}
	
	public String voltar(){

		if(this.voltarPara != null){
			return voltarPara;
		}
		
		return null;
	}

	@Override
	public List<MbcCirurgias> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return this.blocoCirurgicoFacade.pesquisarCirurgiasRealizadasNotaConsumo(firstResult, maxResult, orderProperty, asc, this.getElemento());
	}

	@Override
	public Long recuperarCount() {
		return this.blocoCirurgicoFacade.pesquisarCirurgiasRealizadasNotaConsumoCount(this.getElemento());
	}

	public String obterProntuarioFormatado(Object valor) {
		return CoreUtil.formataProntuario(valor);
	}
	
	/**
	 * Carrega a tela de edição
	 * 
	 * @return
	 */
	public String editarRegistroCirurgiaRealizada(Integer crgSeq) {
		this.crgSeq = crgSeq;
		registroCirurgiaRealizadaController.setCrgSeq(crgSeq);
		registroCirurgiaRealizadaController.setVoltarPara(PAGE_PESQUISA_REGISTRO_CIRURGIA_REALIZADA);	
		registroCirurgiaRealizadaController.inicio();
		return PAGE_REGISTRO_CIRUGIA_REALIZADA;
	}

	public String redirecionarPesquisaFonetica() {
		this.paciente = null;
		this.prontuario = null;
		this.pacCodigoFonetica = null;
		this.dataModel.limparPesquisa();
		this.pesquisaPacienteController.setCameFrom("blococirurgico-pesquisaCirurgiaRealizadaNotaConsumo");
		return PESQUISA_FONETICA;
	}
	
	/*
	 * Getters e Setters
	 */
	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public AghUnidadesFuncionais getUnidadeCirurgica() {
		return unidadeCirurgica;
	}

	public void setUnidadeCirurgica(AghUnidadesFuncionais unidadeCirurgica) {
		this.unidadeCirurgica = unidadeCirurgica;
	}

	public Date getDataCirurgia() {
		return dataCirurgia;
	}

	public void setDataCirurgia(Date dataCirurgia) {
		this.dataCirurgia = dataCirurgia;
	}

	public Short getNumeroAgenda() {
		return numeroAgenda;
	}

	public void setNumeroAgenda(Short numeroAgenda) {
		this.numeroAgenda = numeroAgenda;
	}

	public DominioSimNao getIndDigtNotaSala() {
		return indDigtNotaSala;
	}

	public void setIndDigtNotaSala(DominioSimNao indDigtNotaSala) {
		this.indDigtNotaSala = indDigtNotaSala;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
		if (paciente != null && StringUtils.isNotBlank(paciente.getNome())) {
			this.nomePaciente = paciente.getNome();
			this.pacCodigoFonetica = paciente.getCodigo();
			this.prontuario = paciente.getProntuario();
		} else {
			this.nomePaciente = null;
			this.pacCodigoFonetica = null;
			this.prontuario = null;
		}
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}
	
	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Boolean getIniciaPesquisando() {
		return iniciaPesquisando;
	}

	public void setIniciaPesquisando(Boolean iniciaPesquisando) {
		this.iniciaPesquisando = iniciaPesquisando;
	}
	 


	public DynamicDataModel<MbcCirurgias> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MbcCirurgias> dataModel) {
	 this.dataModel = dataModel;
	}
}