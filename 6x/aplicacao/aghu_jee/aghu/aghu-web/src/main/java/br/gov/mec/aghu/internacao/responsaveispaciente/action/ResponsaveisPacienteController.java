package br.gov.mec.aghu.internacao.responsaveispaciente.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
//TODO
//import org.hibernate.validator.ClassValidator; 
//import org.hibernate.validator.InvalidValue;
import javax.ejb.EJB;
import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioResponsavelConta;
import br.gov.mec.aghu.dominio.DominioTipoResponsabilidade;
import br.gov.mec.aghu.exames.cadastrosapoio.action.ResponsavelContaController;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.vo.ResponsavelVO;
import br.gov.mec.aghu.internacao.action.CadastroInternacaoController;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.responsaveispaciente.business.IResponsaveisPacienteFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghResponsavel;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinResponsaveisPaciente;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipUfs;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * Classe responsável por controlar as ações de criação e edição de
 * responsável de paciente.
 */
public class ResponsaveisPacienteController extends ActionController {

	private static final Log LOG = LogFactory.getLog(ResponsaveisPacienteController.class);
	private static final String PAGE_CADASTRO_INTERNACAO = "internacao-cadastroInternacao";
	private static final String PAGE_LISTA_RESPONSAVEIS_PACIENTE = "listaResponsaveisPaciente";
	private static final String PAGE_CADASTRO_RESPONSAVEIS_PACIENTES = "cadastroResponsaveisPaciente";
	
	
	private static final long serialVersionUID = 5134114319486697349L;

	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IResponsaveisPacienteFacade responsaveisPacienteFacade;
	
	@Inject
	private CadastroInternacaoController cadastroInternacaoController;
	
	@Inject
	private ResponsavelContaController responsavelContaController; 
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	//@EJB
	private AinInternacao internacao;
	
	/**
	 * Seq da Internação, obtido via page parameter.
	 */
	private Integer ainInternacaoSeq;
	
	/**
	 * Código do Paciente, obtido via page parameter.
	 */
	private Integer aipPacCodigo;
	
	/**
	 * Responsável a ser criado/editado
	 */
	private AinResponsaveisPaciente responsavelPaciente;
	
	private DominioResponsavelConta selRespConta;
	
	private String cepString;
	
	private Boolean recarregar = false;
	
	private Boolean pacienteMenorDeIdade = false;
	
	/**
	 * Lista de responsáveis associados a este paciente.
	 */
	private List<AinResponsaveisPaciente> listaResponsaveisPaciente = null;
	
	/**
	 * Lista de responsáveis do paciente que foram excluídos.
	 */
	private List<AinResponsaveisPaciente> listaResponsaveisPacienteExcluidos = null;
	
	private ResponsavelVO responsavelVo;
	
	private AghResponsavel responsavelContaRetorno;
	
	private static final String RESPONSAVEL_CONTA_CRUD = "exames-responsavelContaCRUD";
	private static final String INTERNACAO_LISTA_RESP_PACIENTE = "internacao-cadastroResponsaveisPaciente";
	
	private DominioTipoResponsabilidade tipoResponsabilidade;
	
	private AipPacientes paciente;
	
	private Short codigoConvenio;
	
	private Boolean isSus;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() {
		
		List<AinResponsaveisPaciente> listaResponsaveisBanco = null;
		if (internacao == null && ainInternacaoSeq != null){
			internacao = internacaoFacade.obterInternacaoPorSeq(ainInternacaoSeq);	
			listaResponsaveisBanco = responsaveisPacienteFacade.pesquisarResponsaveisPaciente(ainInternacaoSeq);
		}
		
		if (listaResponsaveisPaciente == null){
			listaResponsaveisPaciente = new ArrayList<AinResponsaveisPaciente>();	
			if (internacao != null && listaResponsaveisBanco != null){
				listaResponsaveisPaciente.addAll(listaResponsaveisBanco);
			}
		}
		
		if (listaResponsaveisPacienteExcluidos == null){
			listaResponsaveisPacienteExcluidos = new ArrayList<AinResponsaveisPaciente>();
		}
		
		if(recarregar) {
			listaResponsaveisBanco = responsaveisPacienteFacade.pesquisarResponsaveisPaciente(ainInternacaoSeq);
			listaResponsaveisPaciente = new ArrayList<AinResponsaveisPaciente>();	
			listaResponsaveisPaciente.addAll(listaResponsaveisBanco);
			recarregar = false;
		}
		
		if (this.getResponsavelContaRetorno() != null){			
			this.setResponsavelVo(this.cadastrosApoioExamesFacade.obterResponsavelVo(this.getResponsavelContaRetorno()));
			this.getResponsavelVo().setAghResponsavel(this.getResponsavelContaRetorno());
		}
		
		if (ainInternacaoSeq != null){
			menorDeIdade(ainInternacaoSeq);
		}
		
		if(codigoConvenio!=null) {
			isSus = false;
			try {
				AghParametros convenioSusParam = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CONVENIO_SUS);
				Short convenioSus = convenioSusParam.getVlrNumerico().shortValue();	
				
				if(codigoConvenio.shortValue()==convenioSus.shortValue()) {
						isSus = true;
				}
			} catch(ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	/**
	 * Método que realiza a ação do botão adicionar na tela de
	 * listagem de responsável por paciente
	 */
	public String prepararAdicionarResponsavelPaciente() {
		this.responsavelPaciente = new AinResponsaveisPaciente();	
		this.setResponsavelVo(null);
		this.setSelRespConta(null);
		this.setResponsavelContaRetorno(null);
		this.setTipoResponsabilidade(null);
		if (aipPacCodigo != null){
			/*** REGRA ADICIONADA A PEDIDO DO ALEX SETAR O NOME DO PACIENTE NO CAMPO NOME DA TABELA AIN_RESPONSAVEIS ***/
			this.setPaciente(this.pacienteFacade.obterPaciente(aipPacCodigo));
		    this.responsavelPaciente.setNome(this.paciente.getNome());
		}
		return PAGE_CADASTRO_RESPONSAVEIS_PACIENTES;
	}
	
	public String salvarInternacaoResponsaveisPaciente(){
		this.cadastroInternacaoController.setListaResponsaveisPaciente(this.listaResponsaveisPaciente);
		this.cadastroInternacaoController.setListaResponsaveisPacienteExcluidos(this.listaResponsaveisPacienteExcluidos);
		String retorno = this.cadastroInternacaoController.verificarCirurgia();
		if(retorno!=null && retorno.equals(PAGE_CADASTRO_INTERNACAO)){
			this.setInternacao(null);
			this.setListaResponsaveisPaciente(null);
		}
		return retorno;
	}
	
	/**
	 * Método que realiza a ação do botão voltar na tela de cadastro de
	 * responsável por paciente
	 */
	public String voltar() {
		this.cadastroInternacaoController.setAipPacCodigo(this.aipPacCodigo);
		this.cadastroInternacaoController.setRetornouTelaAssociada(true);
		this.setTipoResponsabilidade(null);
		this.setInternacao(null);
		this.setListaResponsaveisPaciente(null);
		return PAGE_CADASTRO_INTERNACAO;
	}
	
	
	/**
	 * método que adiciona um responsável pelo paciente.
	 */
	public String incluirResponsavelPaciente() {

		this.responsavelPaciente.setTipoResponsabilidade(this.getTipoResponsabilidade());
		// Caso esteja editando o registro, não altera data de criação
		if (this.responsavelPaciente.getCriadoEm() == null) {
			this.responsavelPaciente.setCriadoEm(new Date());
		}
		
		if (aipPacCodigo != null){
			/*** REGRA ADICIONADA A PEDIDO DO ALEX SETAR O NOME DO PACIENTE NO CAMPO NOME DA TABELA AIN_RESPONSAVEIS ***/
		    this.responsavelPaciente.setNome(this.pacienteFacade.obterPaciente(aipPacCodigo).getNome());
		}

		if (this.getResponsavelVo() != null) {
		    this.responsavelPaciente.setResponsavelConta(this.getResponsavelVo().getAghResponsavel());
		}
		
		if (!pacienteContemResponsavelConta()) {
			this.listaResponsaveisPaciente.add(this.responsavelPaciente);
		}
			
			cepString = null;
			this.cadastroInternacaoController.setListaResponsaveisPaciente(this.listaResponsaveisPaciente);
			this.cadastroInternacaoController.setListaResponsaveisPacienteExcluidos(this.listaResponsaveisPacienteExcluidos);
			this.setTipoResponsabilidade(null);
			return PAGE_LISTA_RESPONSAVEIS_PACIENTE;
	}
	
	private Boolean pacienteContemResponsavelConta() {
			
			for (AinResponsaveisPaciente responsavel : listaResponsaveisPaciente) {
				if(responsavel.getResponsavelConta() != null){
					if (responsavel.getResponsavelConta().equals(this.responsavelPaciente.getResponsavelConta())) {
						return Boolean.TRUE;
					}
				}
			}
			return Boolean.FALSE;	
		}

	/**
	 * método que edita um responsável pelo paciente.
	 */
	public String editarResponsavelPaciente(final AinResponsaveisPaciente responsavelPaciente) {
		this.responsavelPaciente = responsavelPaciente;
		if(responsavelPaciente != null){
		   this.setTipoResponsabilidade(this.responsavelPaciente.getTipoResponsabilidade());
		}
		if(responsavelPaciente.getResponsavelConta() != null){
	       this.responsavelPaciente.setResponsavelConta(this.cadastrosApoioExamesFacade.obterResponsavelPorSeq(responsavelPaciente.getResponsavelConta().getSeq()));
	       this.setResponsavelContaRetorno(this.responsavelPaciente.getResponsavelConta());	       
	       this.setResponsavelVo(this.cadastrosApoioExamesFacade.obterResponsavelVo(responsavelPaciente.getResponsavelConta()));		   
		   this.setSelRespConta(this.responsavelPaciente.getResponsavelConta().getAipPaciente()!= null ? DominioResponsavelConta.P : DominioResponsavelConta.O);
		}
		else {
			 this.setResponsavelVo(null);
			 this.setSelRespConta(null);
			 this.setResponsavelContaRetorno(null);
			 this.setTipoResponsabilidade(null);
		}
		/*** REGRA ADICIONADA A PEDIDO DO ALEX SETAR O NOME DO PACIENTE NO CAMPO NOME DA TABELA AIN_RESPONSAVEIS ***/
		if (aipPacCodigo != null){
			/*** REGRA ADICIONADA A PEDIDO DO ALEX SETAR O NOME DO PACIENTE NO CAMPO NOME DA TABELA AIN_RESPONSAVEIS ***/
			this.setPaciente(this.pacienteFacade.obterPaciente(aipPacCodigo));
		    this.responsavelPaciente.setNome(this.paciente.getNome());
		}
		return PAGE_CADASTRO_RESPONSAVEIS_PACIENTES;
	}
	
	/**
	 * método que remove um responsável pelo paciente.
	 */
	public void removerResponsavelPaciente(final AinResponsaveisPaciente responsavelPaciente) {
		this.listaResponsaveisPacienteExcluidos.add(responsavelPaciente);
		this.listaResponsaveisPaciente.remove(responsavelPaciente);
		this.cadastroInternacaoController.setListaResponsaveisPaciente(this.listaResponsaveisPaciente);
		this.cadastroInternacaoController.setListaResponsaveisPacienteExcluidos(this.listaResponsaveisPacienteExcluidos);
	}
	
	public Boolean menorDeIdade(Integer seqInternacao) {
		AinInternacao internacaoMenorIdade =  internacaoFacade.obterInternacaoPacientePorSeq(seqInternacao);
		if ((this.listaResponsaveisPaciente.size() <= 1) && (DateUtil.getIdade(internacaoMenorIdade.getPaciente().getDtNascimento()) < 18)) {
			pacienteMenorDeIdade = true;
		} else {
			pacienteMenorDeIdade = false;
		}
		return pacienteMenorDeIdade;
	}
	
	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de
	 * responsável por paciente.
	 */
	public String cancelar() {
		this.setResponsavelVo(null);
		this.setSelRespConta(null);
		this.setTipoResponsabilidade(null);
		LOG.info("cancelado");
		return PAGE_LISTA_RESPONSAVEIS_PACIENTE;
		
		
	}
	
	public void redirecionarResponsavel(){
		if(DominioResponsavelConta.P.equals(this.selRespConta)){		   
		   this.setResponsavelVo(null);
		   this.setResponsavelContaRetorno(null);
		   responsavelContaController.setPaciente(this.pacienteFacade.obterPaciente(aipPacCodigo));
		   responsavelContaController.setSelRespConta(this.selRespConta);
		   responsavelContaController.setVoltarPara(INTERNACAO_LISTA_RESP_PACIENTE);
		   responsavelContaController.setSeq(null);
		   
		   NavigationHandler navigationHandler = FacesContext.
			               getCurrentInstance().getApplication().getNavigationHandler();
			        	navigationHandler.handleNavigation(FacesContext.getCurrentInstance(), null, RESPONSAVEL_CONTA_CRUD);
		}
		this.closeDialog("loadDialogWG");	
	}
	
	public Boolean desabilitaResponsavelOutros(){
		 return !DominioResponsavelConta.O.equals(selRespConta);
	}
	
	public List<ResponsavelVO> listarResponsavel(String parametro) {
		return this.cadastrosApoioExamesFacade.listarResponsavel(parametro);
	}
	
	public String addResponsavel() {
		return RESPONSAVEL_CONTA_CRUD;
	}
	
	public void posSelectionResponsavel(){
		if(this.responsavelPaciente != null){
		    this.responsavelPaciente.setResponsavelConta(this.getResponsavelVo().getAghResponsavel());
		}
	}
	
	public String obterNomeResponsavel(AinResponsaveisPaciente respPac){
		String nomeRet ="";
		
		if (respPac != null){			
		    if (respPac.getResponsavelConta()!= null) {
		    	AghResponsavel responsavel = this.cadastrosApoioExamesFacade.obterResponsavelPorSeq(respPac.getResponsavelConta().getSeq());
		    	nomeRet = responsavel.getNome();
		    	if (StringUtils.isBlank(nomeRet)){
		    		AipPacientes pacResponsavel = this.pacienteFacade.obterPaciente(responsavel.getAipPaciente().getCodigo());
		    		nomeRet = pacResponsavel.getNome();
		    	}
		    }
		    else {
		    	nomeRet = respPac.getNome();
		    }
		}
		
		return nomeRet;
	}
	
	public boolean isMostrarLinkExcluirUF() {
		return this.responsavelPaciente.getUf() != null;
	}
	
	public List<AipUfs> pesquisarPorSiglaNome(final String paramPesquisa) {
		return this.returnSGWithCount(this.cadastrosBasicosPacienteFacade.pesquisarPorSiglaNome(paramPesquisa),obterCountUfPorSiglaNome(paramPesquisa));
	}

	public Long obterCountUfPorSiglaNome(final String paramPesquisa) {
		return this.cadastrosBasicosPacienteFacade.obterCountUfPorSiglaNome(paramPesquisa);
	}
	
	
	// ### GETs e SETs ###
	
	public AinResponsaveisPaciente getResponsavelPaciente() {
		return responsavelPaciente;
	}

	public void setResponsavelPaciente(final AinResponsaveisPaciente responsavelPaciente) {
		this.responsavelPaciente = responsavelPaciente;
	}

	/**
	 * @return the listaResponsaveisPaciente
	 */
	public List<AinResponsaveisPaciente> getListaResponsaveisPaciente() {
		return listaResponsaveisPaciente;
	}

	/**
	 * @param listaResponsaveisPaciente
	 *            the listaResponsaveisPaciente to set
	 */
	public void setListaResponsaveisPaciente(
			final List<AinResponsaveisPaciente> listaResponsaveisPaciente) {
		this.listaResponsaveisPaciente = listaResponsaveisPaciente;
	}


	public List<AinResponsaveisPaciente> getListaResponsaveisPacienteExcluidos() {
		return listaResponsaveisPacienteExcluidos;
	}


	public void setListaResponsaveisPacienteExcluidos(
			final List<AinResponsaveisPaciente> listaResponsaveisPacienteExcluidos) {
		this.listaResponsaveisPacienteExcluidos = listaResponsaveisPacienteExcluidos;
	}


	public AinInternacao getInternacao() {
		return internacao;
	}


	public void setInternacao(final AinInternacao internacao) {
		this.internacao = internacao;
	}


	public Integer getAinInternacaoSeq() {
		return ainInternacaoSeq;
	}


	public void setAinInternacaoSeq(final Integer ainInternacaoSeq) {
		this.ainInternacaoSeq = ainInternacaoSeq;
	}


	public Integer getAipPacCodigo() {
		return aipPacCodigo;
	}


	public void setAipPacCodigo(final Integer aipPacCodigo) {
		this.aipPacCodigo = aipPacCodigo;
	}


	public String getCepString() {
		return cepString;
	}


	public void setCepString(final String cepString) {
		this.cepString = cepString;
	}


	public Boolean getRecarregar() {
		return recarregar;
	}


	public void setRecarregar(Boolean recarregar) {
		this.recarregar = recarregar;
	}

	public DominioResponsavelConta getSelRespConta() {
		return selRespConta;
	}

	public void setSelRespConta(DominioResponsavelConta selRespConta) {
		this.selRespConta = selRespConta;
	}

	public ResponsavelVO getResponsavelVo() {
		return responsavelVo;
	}

	public void setResponsavelVo(ResponsavelVO responsavelVo) {
		this.responsavelVo = responsavelVo;
	}

	public AghResponsavel getResponsavelContaRetorno() {
		return responsavelContaRetorno;
	}

	public void setResponsavelContaRetorno(AghResponsavel responsavelContaRetorno) {
		this.responsavelContaRetorno = responsavelContaRetorno;
	}

	public DominioTipoResponsabilidade getTipoResponsabilidade() {
		return tipoResponsabilidade;
	}

	public void setTipoResponsabilidade(
			DominioTipoResponsabilidade tipoResponsabilidade) {
		this.tipoResponsabilidade = tipoResponsabilidade;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
	
	public Boolean getIsSus() {
		return isSus;
	}

	public void setIsSus(Boolean isSus) {
		this.isSus = isSus;
	}
	
	public Boolean getPacienteMenorDeIdade() {
		return pacienteMenorDeIdade;
	}

	public void setPacienteMenorDeIdade(Boolean pacienteMenorDeIdade) {
		this.pacienteMenorDeIdade = pacienteMenorDeIdade;
	}
	
}
