package br.gov.mec.aghu.compras.autfornecimento.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.constantes.EntregasGlobaisAcesso;
import br.gov.mec.aghu.compras.vo.ProgramacaoEntregaGlobalFornecedoresVO;
import br.gov.mec.aghu.compras.vo.ProgramacaoEntregaGlobalTotalizadorVO;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.action.ActionController;


public class ProgramacaoEntregaGlobalFornecedoresController extends ActionController {
	
	private static final long serialVersionUID = 8462762607613893719L;
	private static final String PESQUISAR_PROG_ENTREGA_ITENS_AF_PARCELAS = "pesquisarProgEntregaItensAFParcelas";
	private static final String PESQUISA_AUT_FORNECIMENTO_ENTREGA_PROGRAMADA = "pesquisaAutFornecimentoEntregaProgramada";

	private List<ProgramacaoEntregaGlobalFornecedoresVO> listaProgramacaoEntregaGlobalFornecedores;
	
	private Integer codigoGrupoMaterial;
	private String descricaoGrupoMaterial;
	private String grupoMaterial;
	private Date dataInicial;
	private Date dataFinal;
	private String tipoValor;
	private String tipoValorSelecionado;
	private String voltarPara;
	private Boolean executarInicio = true;
	private ScoFornecedor fornecedor;
	private String descricaoFornecedor;
	
	
	private ProgramacaoEntregaGlobalFornecedoresVO programacaoEntregaGlobalFornecedoresVOSelecionado;
	
	private ProgramacaoEntregaGlobalTotalizadorVO programacaoEntregaGlobalTotalizadorVO;
	
	@Inject
	private PesquisaAutFornecimentoEntregaProgramadaController pesquisaAutFornecimentoEntregaProgramadaController;

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {

		if(executarInicio){
			if (fornecedor != null){
				descricaoFornecedor = fornecedor.getNumero().toString()+" - "+fornecedor.getRazaoSocial();
			}else{
				descricaoFornecedor = null;
			}
			programacaoEntregaGlobalTotalizadorVO = new ProgramacaoEntregaGlobalTotalizadorVO();
			listaProgramacaoEntregaGlobalFornecedores = autFornecimentoFacade.listarProgramacaoEntregaGlobalFornecedores(codigoGrupoMaterial, dataInicial, dataFinal, tipoValor, programacaoEntregaGlobalTotalizadorVO, fornecedor);	
		}
	
	}
	

	public String voltar() {
		setDescricaoGrupoMaterial(null);
		setTipoValor(null);
		setExecutarInicio(true);
		return voltarPara;
	}
	
	public String irAutorizacaoFornecimentoSaldoProgramado(ProgramacaoEntregaGlobalFornecedoresVO programacaoEntregaGlobalFornecedoresVO) {
		return irAutorizacaoFornecimento(programacaoEntregaGlobalFornecedoresVO, EntregasGlobaisAcesso.SALDO_PROGRAMADO.toString());
	}
	
	public String irAutorizacaoFornecimentoValorALiberar(ProgramacaoEntregaGlobalFornecedoresVO programacaoEntregaGlobalFornecedoresVO) {
		return irAutorizacaoFornecimento(programacaoEntregaGlobalFornecedoresVO, EntregasGlobaisAcesso.VALOR_LIBERAR.toString());
	}
	
	public String irAutorizacaoValorLiberado(ProgramacaoEntregaGlobalFornecedoresVO programacaoEntregaGlobalFornecedoresVO) {
		return irAutorizacaoFornecimento(programacaoEntregaGlobalFornecedoresVO, EntregasGlobaisAcesso.VALOR_LIBERADO.toString());
	}
	
	public String irAutorizacaoValorEmAtraso(ProgramacaoEntregaGlobalFornecedoresVO programacaoEntregaGlobalFornecedoresVO) {
		return irAutorizacaoFornecimento(programacaoEntregaGlobalFornecedoresVO, EntregasGlobaisAcesso.VALOR_ATRASO.toString());
	}
	public String irAutorizacaoFornecimento(ProgramacaoEntregaGlobalFornecedoresVO programacaoEntregaGlobalFornecedoresVO){
		return this.irAutorizacaoFornecimento(programacaoEntregaGlobalFornecedoresVO, null);
	}
	private String irAutorizacaoFornecimento(ProgramacaoEntregaGlobalFornecedoresVO programacaoEntregaGlobalFornecedoresVO, String tipoValor) {
		this.programacaoEntregaGlobalFornecedoresVOSelecionado = programacaoEntregaGlobalFornecedoresVO;
		pesquisaAutFornecimentoEntregaProgramadaController.setTipoValorSelecionado(tipoValor);
		return PESQUISA_AUT_FORNECIMENTO_ENTREGA_PROGRAMADA;
	}
	
	public String irProgramacao(ProgramacaoEntregaGlobalFornecedoresVO programacaoEntregaGlobalFornecedoresVO) {
		this.programacaoEntregaGlobalFornecedoresVOSelecionado = programacaoEntregaGlobalFornecedoresVO;
		return PESQUISAR_PROG_ENTREGA_ITENS_AF_PARCELAS;
	}
	
	public List<ProgramacaoEntregaGlobalFornecedoresVO> getListaProgramacaoEntregaGlobalFornecedores() {
		return listaProgramacaoEntregaGlobalFornecedores;
	}

	public void setListaProgramacaoEntregaGlobalFornecedores(
			List<ProgramacaoEntregaGlobalFornecedoresVO> listaProgramacaoEntregaGlobalFornecedores) {
		this.listaProgramacaoEntregaGlobalFornecedores = listaProgramacaoEntregaGlobalFornecedores;
	}

	public String getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(String grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getTipoValor() {
		return tipoValor;
	}

	public void setTipoValor(String tipoValor) {
		this.tipoValor = tipoValor;
	}

	public ProgramacaoEntregaGlobalTotalizadorVO getProgramacaoEntregaGlobalTotalizadorVO() {
		return programacaoEntregaGlobalTotalizadorVO;
	}

	public void setProgramacaoEntregaGlobalTotalizadorVO(
			ProgramacaoEntregaGlobalTotalizadorVO programacaoEntregaGlobalTotalizadorVO) {
		this.programacaoEntregaGlobalTotalizadorVO = programacaoEntregaGlobalTotalizadorVO;
	}

	public String getTipoValorSelecionado() {
		return tipoValorSelecionado;
	}

	public void setTipoValorSelecionado(String tipoValorSelecionado) {
		this.tipoValorSelecionado = tipoValorSelecionado;
	}

	public ProgramacaoEntregaGlobalFornecedoresVO getProgramacaoEntregaGlobalFornecedoresVOSelecionado() {
		return programacaoEntregaGlobalFornecedoresVOSelecionado;
	}

	public void setProgramacaoEntregaGlobalFornecedoresVOSelecionado(
			ProgramacaoEntregaGlobalFornecedoresVO programacaoEntregaGlobalFornecedoresVOSelecionado) {
		this.programacaoEntregaGlobalFornecedoresVOSelecionado = programacaoEntregaGlobalFornecedoresVOSelecionado;
	}

	public Integer getCodigoGrupoMaterial() {
		return codigoGrupoMaterial;
	}

	public void setCodigoGrupoMaterial(Integer codigoGrupoMaterial) {
		this.codigoGrupoMaterial = codigoGrupoMaterial;
	}

	public IAutFornecimentoFacade getAutFornecimentoFacade() {
		return autFornecimentoFacade;
	}

	public void setAutFornecimentoFacade(
			IAutFornecimentoFacade autFornecimentoFacade) {
		this.autFornecimentoFacade = autFornecimentoFacade;
	}

	public String getDescricaoGrupoMaterial() {
		return descricaoGrupoMaterial;
	}

	public void setDescricaoGrupoMaterial(String descricaoGrupoMaterial) {
		this.descricaoGrupoMaterial = descricaoGrupoMaterial;
	}

	public void setExecutarInicio(Boolean executarInicio) {
		this.executarInicio = executarInicio;
	}

	public Boolean getExecutarInicio() {
		return executarInicio;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	public String getDescricaoFornecedor() {
		return descricaoFornecedor;
	}

	public void setDescricaoFornecedor(String descricaoFornecedor) {
		this.descricaoFornecedor = descricaoFornecedor;
	}
	
}
