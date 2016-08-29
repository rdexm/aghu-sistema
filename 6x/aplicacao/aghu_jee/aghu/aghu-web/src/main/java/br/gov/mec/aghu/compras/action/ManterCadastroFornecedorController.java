package br.gov.mec.aghu.compras.action;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.math.NumberUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioClassEcon;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoFornc;
import br.gov.mec.aghu.dominio.DominioTipoFornecedor;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipCidades;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;



public class ManterCadastroFornecedorController extends ActionController{
	
	private static final long serialVersionUID = 631026886886170211L;
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private ScoFornecedor fornecedor;
	private List<AipCidades> cidades;
	
	private DominioClassEcon classEcon;
	private DominioTipoFornc tipoFornecedor;
	
	private Integer numeroFrn;
	
	private Integer diaFavEntgMaterial;
	private Integer ddd;
	private Integer ddi; 
	private Integer fone;
	private Integer fax;
	private Integer inscEst;
	
	private DominioSimNao frnAtivo = DominioSimNao.S;
	private DominioSimNao nacional = DominioSimNao.S;
	
	
	AghParametros parametroClassEcon = null;
	AghParametros parametroTipoFornecedor = null; 
	
	private Boolean edicao = false;
	
	private static final String COR_SINAL_VERMELHO ="vermelho";
	private static final String COR_SINAL_AMARELO ="amarelo";
	private static final String PESQUISAR_FORNECEDOR = "pesquisarFornecedor";
	private static final String CADASTRAR_CONTATO_FORNECEDOR = "cadastrarContatoFornecedor";
	private static final String MARCA_FORNECEDOR = "estoque-manterCadastroMarcasFornecedor";
	private static final String PENALIDADE_FORNECEDOR = "estoque-penalidadeFornecedor";
	private static final String CADASTRAR_SENHA = "estoque-cadastrarSenha";
	private static final String MATERIAIS_RAMOS_FORNECEDOR ="compras-materiaisRamosFornecedor";
	private static final String IR_CADASTRAR_SOCIO_FORNECEDOR ="estoque-pesquisarSociosFornecedores";
	private static final String CERTIFICADO_REGISTRO_CADASTRAL ="compras-certificadoRegistroCadastral";
	
	private String fromCompradorAF;
	private String voltarPara;

	private enum ManterCadastroFornecedorControllerExceptionCode implements BusinessExceptionCode{
		ERRO_CNPJ_CPF_MINIMO_8_CHARS,
		ERRO_DT_DEVE_SER_FUTURA,
		ERRO_NUMERO_INVALIDO,
		FORNECEDOR_GRAVADO_COM_SUCESSO;
	}
	
	private enum TargetFornecedorEnum{
		VOLTAR, VOLTAR_COMP_AF;
	}

	public String voltarCompradorAF() {
		this.fornecedor = null;
		this.numeroFrn = null;
		return  TargetFornecedorEnum.VOLTAR_COMP_AF.toString();
	}

	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public void inicio(){

		
		try {
			if(parametroClassEcon == null){
				parametroClassEcon = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CLASSIF_ECON_FORNECEDOR);
			}
			
			if(parametroTipoFornecedor == null){
				parametroTipoFornecedor = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CLASSIF_TIPO_FORNECEDOR);
			}
			
			if(numeroFrn != null){
				//Obtem o fornecedor, é edição
				fornecedor = comprasFacade.obterFornecedorPorNumero(numeroFrn);
				
				if(fornecedor != null && fornecedor.getDdd() != null) {
					setDdd(Integer.valueOf(fornecedor.getDdd()));
				}
				if(fornecedor != null && fornecedor.getDdi() != null) {
					setDdi(Integer.valueOf(fornecedor.getDdi()));
				}
				
				if(fornecedor != null && fornecedor.getFone() != null){
					setFone(fornecedor.getFone().intValue());
				}
				
				if(fornecedor != null && fornecedor.getFax() != null){
					setFax(fornecedor.getFax().intValue());
				}
				
				frnAtivo= obterDominioSimNaoAtivo(fornecedor.getSituacao());
				nacional = obterTipoNacionalidadeFornecedor(fornecedor.getTipoFornecedor());
				
				if(fornecedor.getClassificacaoEconomica() != null && !fornecedor.getClassificacaoEconomica().isEmpty()){
					classEcon = DominioClassEcon.valueOf(fornecedor.getClassificacaoEconomica());
				}
				
				if(fornecedor.getClassificacao() != null && !fornecedor.getClassificacaoEconomica().isEmpty()){					
					tipoFornecedor = DominioTipoFornc.valueOf(fornecedor.getClassificacao());					
				}
				
				edicao = true;
				
			} else{
				fornecedor = new ScoFornecedor();
				fornecedor.setTipoFornecedor(DominioTipoFornecedor.FNA);
				frnAtivo = DominioSimNao.S;
				nacional = DominioSimNao.S;
				classEcon = null;
				tipoFornecedor = null;
				ddd = null;
				ddi = null;
				diaFavEntgMaterial = null;
				fone = null;
				fax = null;
			}
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	
	}
	
	
	public String obterContatoFornecedor(){
		return CADASTRAR_CONTATO_FORNECEDOR;
	}
	
	public String obterMarcaFornecedor(){
		return MARCA_FORNECEDOR;
	}
	
	public String obterPenalidadeFornecedor(){
		return PENALIDADE_FORNECEDOR;
	}	
	
	public String cadastrarSenha() {
		return CADASTRAR_SENHA;
	}
	
	public String obterMateriaisRamosFornecedor(){
		return MATERIAIS_RAMOS_FORNECEDOR;
	}
	
	private DominioSimNao obterDominioSimNaoAtivo(DominioSituacao situacao){
		if(situacao != null){
			if(DominioSituacao.A.equals(situacao)){
				return DominioSimNao.S;
			}
			else{
				return DominioSimNao.N;
			}
		}
		else{
			return null;
		}
	}
	
	private DominioTipoFornecedor obterTipoNacionalidadeFornecedor(
			DominioSimNao nacional) {
		if(nacional.isSim()){
			return DominioTipoFornecedor.FNA;
		}
		else{
			return DominioTipoFornecedor.FNE;
		}
	}
	
	private DominioSimNao obterTipoNacionalidadeFornecedor(DominioTipoFornecedor tipoFornecedor){
		if(tipoFornecedor != null){
			if(tipoFornecedor.equals(DominioTipoFornecedor.FNA)){
				return DominioSimNao.S;
			}
			else{
				return DominioSimNao.N;
			}
		}
		else{
			return null;
		}
	}
	
	
	public String getCnpj() {
		if(fornecedor != null && fornecedor.getCgc() != null && fornecedor.getNumero() != null 
				&& !(fornecedor.getNumero().intValue() == fornecedor.getCgc().intValue()
						&& fornecedor.getTipoFornecedor().equals(DominioTipoFornecedor.FNE))) {
			return formatarCNPJ(fornecedor.getCgc().toString());				
		}
		return null;
	}
	
	private String formatarCNPJ(String cnpj){
		String tmp = "00" + cnpj;
		
		if(cnpj != null && cnpj.length() >= 14){
			tmp = tmp.substring(tmp.length()-14);
			//Mascara
			tmp = tmp.substring(0,2) + "." + tmp.substring(2, 5) + "." + tmp.substring(5,8) + "/" + tmp.substring(8,12) + "-" + tmp.substring(12);
		}
		return tmp;
	}

	public void setCnpj(String cnpj) {
		if(cnpj != null && NumberUtils.isDigits(cnpj)){
			fornecedor.setCgc(Long.valueOf(cnpj));
		}
		else{
			fornecedor.setCgc(null);
		}
	}
	
	public String getCpf() {
		if(fornecedor != null && fornecedor.getCpf() != null){
			return fornecedor.getCpf().toString();
		}
		else{
			return null;
		}
	}

	public void setCpf(String cpf) {
		if(cpf != null && NumberUtils.isDigits(cpf)){
			fornecedor.setCpf(Long.valueOf(cpf));
		}
		else{
			fornecedor.setCpf(null);
		}
	}
	
	public List<AipCidades> listarCidades(String strPesquisa){
		return this.returnSGWithCount(cadastrosBasicosPacienteFacade.pesquisarCidades(strPesquisa),listarCidadesCount(strPesquisa));
	} 

	public Integer listarCidadesCount(String strPesquisa){
		if(cidades != null){
			return cidades.size();
		}
		else{
			return 0;
		}
	}
		
	public DominioSimNao getNacional() {
		return nacional;
	}

	public void setNacional(DominioSimNao nacional) {
		this.nacional = nacional;
		fornecedor.setTipoFornecedor(obterTipoNacionalidadeFornecedor(nacional));
	}


	public DominioSimNao getFrnAtivo() {
		return frnAtivo;
	}

	public void setFrnAtivo(DominioSimNao frnAtivo) {
		this.frnAtivo = frnAtivo;
		if(frnAtivo.isSim()){
			fornecedor.setSituacao(DominioSituacao.A);
		}
		else{
			fornecedor.setSituacao(DominioSituacao.I);
		}
	}
	

	public DominioClassEcon getClassEcon() {
		return classEcon;
	}

	public void setClassEcon(DominioClassEcon classEcon) {
		this.classEcon = classEcon;
		if(classEcon != null){
			fornecedor.setClassificacaoEconomica(classEcon.getAbbreviation().toUpperCase());
		}
		else{
			fornecedor.setClassificacaoEconomica(null);
		}
	}

	public DominioTipoFornc getTipoFornecedor() {
		return tipoFornecedor;
	}

	public void setTipoFornecedor(DominioTipoFornc tipoFornecedor) {
		this.tipoFornecedor = tipoFornecedor;
		if(tipoFornecedor != null){
			fornecedor.setClassificacao(tipoFornecedor.toString());
		}
		else{
			fornecedor.setClassificacao(null);
		}
	}
	
	public String gravar(){
		try {
						
			ScoFornecedor fornecedorOrginal = comprasFacade.obterFornecedorPorNumero(fornecedor.getNumero());
			validarDados(fornecedorOrginal);
			
			if (getFornecedor().getCrc() == null && getFornecedor().getDtValidadeCrc() != null ){
			    getFornecedor().setCrc(comprasFacade.obterProximoNumeroCrcFornecedor(fornecedor));
			}
			comprasFacade.persistirFornecedor(fornecedor);
			
			apresentarMsgNegocio(Severity.INFO, ManterCadastroFornecedorControllerExceptionCode.FORNECEDOR_GRAVADO_COM_SUCESSO.toString());
			
			//volta para página de pesquisa
			return voltar();
		} catch (BaseListException e) {
			this.apresentarExcecaoNegocio(e);
			
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
		
		return null;
	}

	@SuppressWarnings("PMD.NPathComplexity")
	private void validarDados(ScoFornecedor fornecedorOrginal) throws BaseListException {
		
		BaseListException listaExcept = new BaseListException();		
		
		//Validar as datas somente quando for o primeiro cadastro, ou quando alterar a data
		if(fornecedorOrginal == null || (fornecedorOrginal != null && fornecedorOrginal.getDtValidadeRecFed() != null && fornecedorOrginal.getDtValidadeRecFed().compareTo(fornecedor.getDtValidadeRecFed()) != 0)){
			validarDataFutura(fornecedor.getDtValidadeRecFed(), listaExcept);
		}
		if(fornecedorOrginal == null || (fornecedorOrginal != null && fornecedorOrginal.getDtValidadeAvs() != null && fornecedorOrginal.getDtValidadeAvs().compareTo(fornecedor.getDtValidadeAvs()) != 0)){
			validarDataFutura(fornecedor.getDtValidadeAvs(),listaExcept);
		}
		if(fornecedorOrginal == null || (fornecedorOrginal != null && fornecedorOrginal.getDtValidadeRecEst() != null && fornecedorOrginal.getDtValidadeRecEst().compareTo(fornecedor.getDtValidadeRecEst()) != 0)){
			validarDataFutura(fornecedor.getDtValidadeRecEst(),listaExcept);
		}
		if(fornecedorOrginal == null || (fornecedorOrginal != null && fornecedorOrginal.getDtValidadeRecMun() != null && fornecedorOrginal.getDtValidadeRecMun().compareTo(fornecedor.getDtValidadeRecMun()) != 0)){
			validarDataFutura(fornecedor.getDtValidadeRecMun(),listaExcept);
		}
		if(fornecedorOrginal == null || (fornecedorOrginal != null && fornecedorOrginal.getDtValidadeBal() != null && fornecedorOrginal.getDtValidadeBal().compareTo(fornecedor.getDtValidadeBal()) != 0)){
			validarDataFutura(fornecedor.getDtValidadeBal(),listaExcept);
		}
		if(fornecedorOrginal == null || (fornecedorOrginal != null && fornecedorOrginal.getDtValidadeCNDT() != null && fornecedorOrginal.getDtValidadeCNDT().compareTo(fornecedor.getDtValidadeCNDT()) != 0)){
			validarDataFutura(fornecedor.getDtValidadeCNDT(),listaExcept);
		}
		
//		//Se for extrangeiro, garante que a inscricao estadual está nula
//		if(fornecedor.getTipoFornecedor().equals(DominioTipoFornecedor.FNE)){
//			fornecedor.setInscricaoEstadual(null);
//		}
				
		if(listaExcept.hasException()){
			throw listaExcept;
		}
	}
	

	public String voltar(){
		this.fornecedor = null;
		this.numeroFrn = null;
		
		if(voltarPara != null){
			return voltarPara;
			
		} else {
			return PESQUISAR_FORNECEDOR;
		}
	}

	public Integer getNumeroFrn() {
		return numeroFrn;
	}

	public void setNumeroFrn(Integer numeroFrn) {
		this.numeroFrn = numeroFrn;
	}

	public Boolean getEdicao() {
		return edicao;
	}
	
	private void validarDataFutura(Date dataValidar, BaseListException listaExcept) {
		Date dataAtual = new Date();
		if(dataValidar != null && dataValidar.before(dataAtual)){
			listaExcept.add(new ApplicationBusinessException(ManterCadastroFornecedorControllerExceptionCode.ERRO_DT_DEVE_SER_FUTURA));
		}
	} 

	public Boolean isVencido(Date data) {
		
		Boolean vencido = Boolean.FALSE;
		Date dataAtual = new Date();
		Calendar cal = Calendar.getInstance();
		
		//Zera a hora do dia
		cal.setTime(dataAtual);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		dataAtual = cal.getTime();
		
		if(data != null && data.before(dataAtual)) {
			vencido = Boolean.TRUE;
		}
		return vencido;		
	}
	
	public Boolean aVencer(Date data) {
		
		Boolean aVencer = Boolean.FALSE;
		Date dataAtual = new Date();

		if(!isVencido(data)){
			if(data != null && dataAtual != null) {
				if(CoreUtil.diferencaEntreDatasEmDias(data, dataAtual) < 15) {
					aVencer = Boolean.TRUE;
				}
			}
		}
		return aVencer;
	}
	
	public boolean isExistePenalidade(){
		if(fornecedor != null){
			return comprasFacade.existePenalidade(fornecedor);
		}
		return false;
	}
	
	public String getCadastrarSocio(){
		return IR_CADASTRAR_SOCIO_FORNECEDOR;
	}
	
	public int obterNumeroPenalidades() {
		if (fornecedor !=  null) {
			return comprasFacade.obterNumeroPenalidades(fornecedor);
		}
		return Integer.valueOf(0);
	}
	

	public Long obterNumeroSuspensoes() {
		if (fornecedor !=  null) {
			return comprasFacade.obterNumeroSuspensoes(fornecedor);
		}
		return Long.valueOf(0);
	}
	
	public Long obterNumeroAdvertencias() {
		if (fornecedor !=  null) {
			return comprasFacade.obterNumeroAdvertencias(fornecedor);
		}
		return Long.valueOf(0);
	}
	
	public Long obterNumeroOcorrencias() {
		if (fornecedor !=  null) {
			return comprasFacade.obterNumeroOcorrencias(fornecedor);
		}
		return Long.valueOf(0);
	}	
	
	public Long obterNumeroMulta() {
		if (fornecedor !=  null) {
			return comprasFacade.obterNumeroMulta(fornecedor);
		}
		return Long.valueOf(0);
	}
	
	public String getImprimirCrc(){
		return CERTIFICADO_REGISTRO_CADASTRAL;
	}	
	
	@SuppressWarnings("PMD.NPathComplexity")
	public String obterCorInput(String id){
		
		StringBuilder retorno = new StringBuilder();
		Date data = new Date();
		
		if(id != null && fornecedor != null) {
			
			if("dtValCRC".equalsIgnoreCase(id)) {
				data = fornecedor.getDtValidadeCrc();
			}
			if("dtValCBP".equalsIgnoreCase(id)) {
				data = fornecedor.getDtValidadeCBP();
			}
			if("dtValFGTS".equalsIgnoreCase(id)) {
				data = fornecedor.getDtValidadeFgts();
			}
			if("dtValINSS".equalsIgnoreCase(id)) {
				data = fornecedor.getDtValidadeInss();
			}
			if("dtValRecFed".equalsIgnoreCase(id)) {
				data = fornecedor.getDtValidadeRecFed();
			}
			if("dtAlvVigSan".equalsIgnoreCase(id)) {
				data = fornecedor.getDtValidadeAvs();
			}
			if("dtValRecEst".equalsIgnoreCase(id)) {
				data = fornecedor.getDtValidadeRecEst();
			}
			if("dtValRecMun".equalsIgnoreCase(id)) {
				data = fornecedor.getDtValidadeRecMun();
			}
			if("dtValCDNT".equalsIgnoreCase(id)) {
				data = fornecedor.getDtValidadeCNDT();
			}
			if("dtValBalPat".equalsIgnoreCase(id)) {
				data = fornecedor.getDtValidadeBal();
			}
		}
		
		if(isVencido(data)){
			retorno.append("input-calendar-");
			retorno.append(COR_SINAL_VERMELHO);
		}
		else if(aVencer(data)){
			retorno.append("input-calendar-");
			retorno.append(COR_SINAL_AMARELO);
		}
		return retorno.toString();
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public String getFromCompradorAF() {
		return fromCompradorAF;
	}

	public void setFromCompradorAF(String fromCompradorAF) {
		this.fromCompradorAF = fromCompradorAF;
	}


	public Integer getDiaFavEntgMaterial() {
		return diaFavEntgMaterial;
	}


	public void setDiaFavEntgMaterial(Integer diaFavEntgMaterial) {
		this.diaFavEntgMaterial = diaFavEntgMaterial;
		if(fornecedor != null && diaFavEntgMaterial != null){
			fornecedor.setDiaFavEntgMaterial(diaFavEntgMaterial.byteValue());
		}				
	}


	public Integer getDdd() {
		return ddd;
	}


	public void setDdd(Integer ddd) {
		this.ddd = ddd;
		if(fornecedor != null && ddd != null){
			fornecedor.setDdd(ddd.shortValue());
		}		
	}


	public Integer getDdi() {
		return ddi;
	}


	public void setDdi(Integer ddi) {
		this.ddi = ddi;
		if(fornecedor != null && ddi != null){
			fornecedor.setDdi(ddi.shortValue());
		}		
	}

	public Integer getFone() {
		return fone;
	}

	public void setFone(Integer fone) {
		this.fone = fone;
		
		if (fornecedor != null){
			if (fone != null){
				fornecedor.setFone(fone.longValue());
			} else {
				fornecedor.setFone(null);
			}
		}	
	}

	public Integer getFax() {
		return fax;
	}

	public void setFax(Integer fax) {
		this.fax = fax;
		
		if (fornecedor != null){
			if (fax != null){
				fornecedor.setFax(fax.longValue());
			} else {
				fornecedor.setFax(null);
			}
		}			
	}

	public Integer getInscEst() {
		return inscEst;
	}

	public void setInscEst(Integer inscEst) {
		this.inscEst = inscEst;
		
		if (fornecedor != null){
			if (inscEst != null){
				fornecedor.setInscricaoEstadual(inscEst.toString());
			} else {
				fornecedor.setInscricaoEstadual(null);
			}
		}					
	} 
}