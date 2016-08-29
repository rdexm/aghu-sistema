package br.gov.mec.aghu.compras.action;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioClassEcon;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoFornc;
import br.gov.mec.aghu.dominio.DominioTipoFornecedor;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoRefCodes;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterCadastroFornecedorPaginatorController extends ActionController implements ActionPaginator{


	@Inject @Paginator
	private DynamicDataModel<AipCidades> dataModel;
	
	private static final long serialVersionUID = -2000369308602040125L;
	private ScoFornecedor fornecedor;
	private ScoFornecedor selecionado;
	private DominioSimNao nacional;
	private DominioSimNao frnAtivo;
	private DominioClassEcon classEcon;
	private DominioTipoFornc tipoFornecedor;
	private List<AipCidades> cidades;
	private Integer ddd;
	private Integer ddi;
	private Long inscEst;
	
	private Integer numeroFornecedor;
	private Integer numeroFornecedorParam;
	
	private static final String COR_SINAL_VERMELHO ="vermelho";
	private static final String COR_SINAL_AMARELO ="amarelo";
	private static final String MANTER_CADASTRO_FORNECEDOR = "manterCadastroFornecedor";
	private static final String CADASTRAR_CONTATO_FORNECEDOR = "cadastrarContatoFornecedor";
	
	private enum ManterFornecedorExceptionCode implements BusinessExceptionCode{
		ERRO_CNPJ_CPF_MINIMO_8_CHARS,
		ERRO_DT_DEVE_SER_FUTURA,
		ERRO_NUMERO_INVALIDO;
	}
	
	private String cpfCnpj;
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private ManterCadastroFornecedorController controller;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	@Override
	public Long recuperarCount() {
		return comprasFacade.pesquisarFornecedorCompletaCount(fornecedor, cpfCnpj);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return comprasFacade.pesquisarFornecedorCompleta(firstResult, maxResult, orderProperty, false, fornecedor, cpfCnpj);
	}
	
	public void inicio(){
	 

	 

		if(fornecedor == null){
			fornecedor = new ScoFornecedor();
			
			//Valor padrão
			nacional = DominioSimNao.S;
			
			ddd = null;
			ddi = null;
			inscEst = null;
			fornecedor.setNumero(numeroFornecedorParam);
		}
		
		numeroFornecedor = null;
		
//		TODO eschweigert rever isso
//		if (this.ativo) {
//			this.dirty = true;
//			this.reiniciarContagem = true;
//		}
	
	}
	

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	
	public String getCpfCnpj() {
		return cpfCnpj;
	}

	public void setCpfCnpj(String cpfCnpj) {
		this.cpfCnpj = cpfCnpj;
	}

	public List<AipCidades> listarCidades(String strPesquisa){
		cidades = cadastrosBasicosPacienteFacade.pesquisarCidades(strPesquisa);
		return this.returnSGWithCount(cadastrosBasicosPacienteFacade.pesquisarCidadePorCodigoNome((String) strPesquisa),listarCidadesCount(strPesquisa));
	}
	
	public Long listarCidadesCount(String strPesquisa){
		return cadastrosBasicosPacienteFacade.pesquisarCidadePorCodigoNomeCount((String) strPesquisa);
	}

	public DominioSimNao getNacional() {
		return nacional;
	}

	public void setNacional(DominioSimNao nacional) {
		this.nacional = nacional;
	}
	
	public DominioSimNao getFrnAtivo() {
		return frnAtivo;
	}

	public void setFrnAtivo(DominioSimNao ativo) {
		this.frnAtivo = ativo;
	}
	
	public List<ScoRefCodes> listarClassEcon(){
		
		try {
			
			String parametro = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_CLASSIF_ECON_FORNECEDOR);
			if(parametro != null){
				List<ScoRefCodes> dominio = comprasFacade.buscarScoRefCodesPorDominio(parametro);
				return dominio;
			}
			
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public List<ScoRefCodes> listarTipoFornecedor(){
		
		try {
			
			String parametro = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_CLASSIF_TIPO_FORNECEDOR);
			if(parametro != null){
				List<ScoRefCodes> dominio = comprasFacade.buscarScoRefCodesPorDominio(parametro);
				return dominio;
			}
			
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public DominioClassEcon getClassEcon() {
		return classEcon;
	}

	public void setClassEcon(DominioClassEcon classEcon) {
		this.classEcon = classEcon;
	}

	public DominioTipoFornc getTipoFornecedor() {
		return tipoFornecedor;
	}

	public void setTipoFornecedor(DominioTipoFornc tipoFornecedor) {
		this.tipoFornecedor = tipoFornecedor;
	}
	
	public String pesquisar(){
		
		try {
			
			//Aribui os valores que ficam salvos no controller ao objeto de pesquisa
			if(this.cpfCnpj != null && !StringUtils.isEmpty(this.cpfCnpj)){
				fornecedor.setCgc(Long.valueOf(this.cpfCnpj));
			}
			else{
				fornecedor.setCgc(null);
			}
			
			if(this.frnAtivo != null){
				DominioSituacao situacao = DominioSituacao.I;
				if(this.frnAtivo.isSim()){
					situacao = DominioSituacao.A;
				}
				fornecedor.setSituacao(situacao);
			}
			else{
				fornecedor.setSituacao(null);
			}
			
			if(this.nacional != null){
				DominioTipoFornecedor tipFornecedor = DominioTipoFornecedor.FNE;
				if(this.nacional.isSim()){
					tipFornecedor = DominioTipoFornecedor.FNA;
				}
				fornecedor.setTipoFornecedor(tipFornecedor);
			}
			else{
				fornecedor.setTipoFornecedor(null);
			}
			
			if(classEcon != null){
				fornecedor.setClassificacaoEconomica(classEcon.getAbbreviation().toUpperCase());
			}
			else{
				fornecedor.setClassificacaoEconomica(null);
			}
			
			if(tipoFornecedor != null){
				fornecedor.setClassificacao(tipoFornecedor.toString().toUpperCase());
			}
			else{
				fornecedor.setClassificacao(null);
			}
			
			//retira, se existirem, "_" nos campos que utilizam mascara
			if(fornecedor.getInscricaoEstadual() != null){
				fornecedor.setInscricaoEstadual(fornecedor.getInscricaoEstadual().replaceAll("_", ""));
			}
			
			if(this.inscEst != null){
				fornecedor.setInscricaoEstadual(this.inscEst.toString());
			}
			
			//valida valores para pesquisa
			validarInputs();
			
			dataModel.reiniciarPaginator();
			Long tot = comprasFacade.pesquisarFornecedorCompletaCount(fornecedor, cpfCnpj);
			
			//redireciona para pagina de edição caso a pesquisa retorne apenas um registro
			if(Long.valueOf(1).equals(tot)){
				
				ScoFornecedor fornAux = (ScoFornecedor) comprasFacade.pesquisarFornecedorCompleta(0, 10, null, false, fornecedor, cpfCnpj).get(0);
				
				numeroFornecedor = fornAux.getNumero();
				controller.setNumeroFrn(numeroFornecedor);
				
				return MANTER_CADASTRO_FORNECEDOR;
			}
			
			
		} catch (NumberFormatException e) {
			apresentarMsgNegocio(Severity.INFO, ManterFornecedorExceptionCode.ERRO_NUMERO_INVALIDO.toString());
			
		} catch (BaseListException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return null;
	}
	
	public String verContatos(){
		return CADASTRAR_CONTATO_FORNECEDOR;
	}
	
	
	public String editar(){
		selecionado = null;
		return MANTER_CADASTRO_FORNECEDOR;
	}
	
	private void validarInputs() throws BaseListException {
		
		BaseListException listaExcept = new BaseListException();

		//Verifica se digitou pelo menos 8 digitos do cnpj ou cpf
		if(this.cpfCnpj != null && this.cpfCnpj.length() > 0) {			
			if(this.cpfCnpj.length() < 8) {
				listaExcept.add(new ApplicationBusinessException(ManterFornecedorExceptionCode.ERRO_CNPJ_CPF_MINIMO_8_CHARS));
			}
		}
		
		
		//Só permite data futura
		validarDataFutura(fornecedor.getDtValidadeFgts(),listaExcept);
		validarDataFutura(fornecedor.getDtValidadeCrc(),listaExcept);
		validarDataFutura(fornecedor.getDtValidadeRecFed(),listaExcept);
		validarDataFutura(fornecedor.getDtValidadeAvs(),listaExcept);
		validarDataFutura(fornecedor.getDtValidadeInss(),listaExcept);
		validarDataFutura(fornecedor.getDtValidadeCBP(),listaExcept);
		validarDataFutura(fornecedor.getDtValidadeRecEst(),listaExcept);
		validarDataFutura(fornecedor.getDtValidadeRecMun(),listaExcept);
		validarDataFutura(fornecedor.getDtValidadeAvsm(),listaExcept);
		validarDataFutura(fornecedor.getDtValidadeAvse(),listaExcept);
		validarDataFutura(fornecedor.getDtValidadeBal(),listaExcept);
		validarDataFutura(fornecedor.getDtValidadeCNDT(),listaExcept);
				
		if(listaExcept.hasException()){
			throw listaExcept;
		}
		
	}


	private void validarDataFutura(Date dataValidar, BaseListException listaExcept) {
		Date dataAtual = new Date();
		if(dataValidar != null && dataValidar.before(dataAtual)){
			listaExcept.add(new ApplicationBusinessException(ManterFornecedorExceptionCode.ERRO_DT_DEVE_SER_FUTURA));
		}
	}

	public void limparPesquisa(){
		dataModel.limparPesquisa();
		
		this.cpfCnpj = null;
		this.fornecedor = new ScoFornecedor();
		this.inscEst = null;
		this.classEcon = null;
		this.tipoFornecedor = null;
		this.nacional = DominioSimNao.S;
		this.frnAtivo = DominioSimNao.S;
	}
	
	public String inserir(){
		return MANTER_CADASTRO_FORNECEDOR;
	}
	
	/**
	 * Trunca o texto, para ser usado nas views.
	 * @param texto
	 * @param tam
	 * @return
	 * @author bruno.mourao
	 * @since 12/03/2012
	 */
	public String truncarTexto(String texto, Object tam){
		Long maxChars = 0l;
		if(tam != null && !StringUtils.isEmpty(tam.toString())){
			maxChars = (Long) tam;
		}
		
		return StringUtils.abbreviate(texto, maxChars.intValue());
		
	}
	
	public Boolean isVencido(ScoFornecedor fornecedor){
		Boolean vencido = false;
		
		Date dataAtual = new Date();
		
		Calendar cal = Calendar.getInstance();
		
		//Zera a hora do dia
		cal.setTime(dataAtual);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		
		dataAtual = cal.getTime();
		
		if((fornecedor.getDtValidadeFgts() != null && fornecedor.getDtValidadeFgts().before(dataAtual)) ||
				(fornecedor.getDtValidadeCrc() != null && fornecedor.getDtValidadeCrc().before(dataAtual)) ||
				(fornecedor.getDtValidadeRecFed() != null && fornecedor.getDtValidadeRecFed().before(dataAtual)) ||
				(fornecedor.getDtValidadeAvs() != null && fornecedor.getDtValidadeAvs().before(dataAtual)) ||
				(fornecedor.getDtValidadeInss() != null && fornecedor.getDtValidadeInss().before(dataAtual)) ||
				(fornecedor.getDtValidadeCBP() != null && fornecedor.getDtValidadeCBP().before(dataAtual)) ||
				(fornecedor.getDtValidadeRecEst() != null && fornecedor.getDtValidadeRecEst().before(dataAtual)) ||
				(fornecedor.getDtValidadeRecMun() != null && fornecedor.getDtValidadeRecMun().before(dataAtual)) ||
				(fornecedor.getDtValidadeBal() != null && fornecedor.getDtValidadeBal().before(dataAtual)) ||
				(fornecedor.getDtValidadeCNDT() != null && fornecedor.getDtValidadeCNDT().before(dataAtual))){
			vencido = true;
		}
		
		return vencido;
		
	}
	
	public Boolean aVencer(ScoFornecedor fornecedor){
		Boolean aVencer = false;
		
		Date dataAtual = new Date();
		
		if(! isVencido(fornecedor)){
					
			if((fornecedor.getDtValidadeFgts() != null && CoreUtil.diferencaEntreDatasEmDias(fornecedor.getDtValidadeFgts(), dataAtual) < 15) ||
					(fornecedor.getDtValidadeCrc() != null && CoreUtil.diferencaEntreDatasEmDias(fornecedor.getDtValidadeCrc(), dataAtual) < 15) ||
					(fornecedor.getDtValidadeRecFed() != null && CoreUtil.diferencaEntreDatasEmDias(fornecedor.getDtValidadeRecFed(), dataAtual) < 15) ||
					(fornecedor.getDtValidadeAvs() != null && CoreUtil.diferencaEntreDatasEmDias(fornecedor.getDtValidadeAvs(), dataAtual) < 15) ||
					(fornecedor.getDtValidadeInss() != null && CoreUtil.diferencaEntreDatasEmDias(fornecedor.getDtValidadeInss(), dataAtual) < 15) ||
					(fornecedor.getDtValidadeCBP() != null && CoreUtil.diferencaEntreDatasEmDias(fornecedor.getDtValidadeCBP(), dataAtual) < 15) ||
					(fornecedor.getDtValidadeRecEst() != null && CoreUtil.diferencaEntreDatasEmDias(fornecedor.getDtValidadeRecEst(), dataAtual) < 15) ||
					(fornecedor.getDtValidadeRecMun() != null && CoreUtil.diferencaEntreDatasEmDias(fornecedor.getDtValidadeRecMun(), dataAtual) < 15) ||
					(fornecedor.getDtValidadeBal() != null && CoreUtil.diferencaEntreDatasEmDias(fornecedor.getDtValidadeBal(), dataAtual) < 15) ||
					(fornecedor.getDtValidadeCNDT() != null && CoreUtil.diferencaEntreDatasEmDias(fornecedor.getDtValidadeCNDT(), dataAtual) < 15)
					){
				aVencer = true;
			}
		}
		return aVencer;
	}

	public String obterCorSemaforo(ScoFornecedor fornecedor){
		String retorno = "";
		
		if(isVencido(fornecedor)){
			retorno = COR_SINAL_VERMELHO;
		}
		else if(aVencer(fornecedor)){
			retorno = COR_SINAL_AMARELO;
		}
		
		return retorno;
	}
	
	public String obterHintSemaforo(ScoFornecedor fornecedor){
		String retorno = "";
		
		if(isVencido(fornecedor)){
			retorno = getBundle().getString("TITLE_SEMAFORO_VENCIDO");
			
		} else if(aVencer(fornecedor)){
			retorno = getBundle().getString("TITLE_SEMAFORO_A_VENCER");
		}
		
		return retorno;
	}

	public Integer getDdd() {
		return ddd;
	}

	public void setDdd(Integer ddd) {
		this.ddd = ddd;
		if(fornecedor != null){
			fornecedor.setDdd(ddd.shortValue());
		}
	}

	public Integer getDdi() {
		return ddi;
	}

	public void setDdi(Integer ddi) {
		this.ddi = ddi;
		if(fornecedor != null){
			fornecedor.setDdi(ddi.shortValue());
		}
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}
	
	public Long getInscEst() {
		return inscEst;
	}

	public void setInscEst(Long inscEst) {
		this.inscEst = inscEst;
		if(this.fornecedor != null && inscEst != null){
			fornecedor.setInscricaoEstadual(inscEst.toString());
		}
	}
 


	public DynamicDataModel<AipCidades> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<AipCidades> dataModel) {
	 this.dataModel = dataModel;
	}

	public ScoFornecedor getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(ScoFornecedor selecionado) {
		this.selecionado = selecionado;
	}
	
	public void setNumeroFornecedorParam(Integer numeroFornecedorParam) {
		this.numeroFornecedorParam = numeroFornecedorParam;
	}

	public Integer getNumeroFornecedorParam() {
		return numeroFornecedorParam;
	}
	
	public String getDescricaoClassificacao(ScoFornecedor fornecedor) {
		if(StringUtils.isEmpty(fornecedor.getClassificacao())){
			return "";
		}
		return DominioTipoFornc.valueOf(fornecedor.getClassificacao()).getDescricao();
	
	}

	public List<AipCidades> getCidades() {
		return cidades;
	}

	public void setCidades(List<AipCidades> cidades) {
		this.cidades = cidades;
	}

}
