package br.gov.mec.aghu.prescricaomedica.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaCompoGrupoComponente;
import br.gov.mec.aghu.model.AfaGrupoComponenteNpt;
import br.gov.mec.aghu.model.AfaTipoComposicoes;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultaCompoGrupoComponenteVO;
import br.gov.mec.aghu.prescricaomedica.vo.ConsultaTipoComposicoesVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public class CadastroTipoComposicaoNPTPaginatorController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2849890746076363715L;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	private List<ConsultaTipoComposicoesVO> listaTipoComposicoes;
	
	private final String CADASTRO_TIPO_COMPOSICOES = "cadastroTipoComposicaoNPTCRUD";
	
	private Boolean inciarPesquisa = Boolean.FALSE;
	
	/**
	 * filtros da pesquisa
	 */
	private Short codigo;
	private Short ordem;
	private DominioSituacao indSituacao;
	private Boolean exibirBotaoNovo = Boolean.FALSE;
	
	private List<ConsultaCompoGrupoComponenteVO> listaGrupoComponente;
	
	// receberá a instancia do grupo de componentes associados
	private ConsultaCompoGrupoComponenteVO listaGrupoComponenteSelecionado;
	
	private AfaTipoComposicoes tipoComposicoesFiltro = new AfaTipoComposicoes();
	private ConsultaTipoComposicoesVO consultaTipoComposicoesVO = new ConsultaTipoComposicoesVO();
	private Boolean editar = Boolean.FALSE;
	
	/**
	 * Receber instancia da linha: Tipo de composição de NPT
	 */
	private ConsultaTipoComposicoesVO consultaTipoComposicoesVOSelecionado;
	
	/**
	 * Recebe valor da sugestion box: Grupo de Componentes 
	 */
	private AfaGrupoComponenteNpt afaGrupoComponenteNpt;
	/**
	 * Receber instancia da linha: Grupo de Componentes de NPT Associados
	 */
	private AfaCompoGrupoComponente afaCompoGrupoComponente;
	private Boolean ativoGrupo = Boolean.FALSE;
	
	//exibir Grupo de Componentes de NPT Associados
	private Boolean exibirVinculo = Boolean.FALSE;
	
	public enum CadastroTipoComposicaoNPTPaginatorControllerExceptionCode implements BusinessExceptionCode {
		TIPO_COMPOSICAO_MS03;
	}

	@PostConstruct
	public void inicializar() {
		this.begin(conversation);
		if(exibirBotaoNovo){
			listaTipoComposicoes();
		}
	}
	
	public void iniciar(){
		if(inciarPesquisa){
			this.pesquisar();
			this.inciarPesquisa = Boolean.FALSE;
		}
	}
	
	public String pesquisar(){
		limparGridsPesquisa();
		filtroPesquisaTiposComposicao();
		listaTipoComposicoes();
		exibirBotaoNovo = Boolean.TRUE;
		return null;
	}
	
	/**
	 * Trunca descrição da Grid.
	 * @param item
	 * @param tamanhoMaximo
	 * @return String truncada.
	 */
	public String obterHint(String item, Integer tamanhoMaximo) {
		
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
		return item;
	}	
	
	/**
	 * Lista principal de Tipos de Composicao de NPT
	 */
	public void listaTipoComposicoes() {
		listaTipoComposicoes =  prescricaoMedicaFacade.pesquisarTiposComposicoesNPT(tipoComposicoesFiltro);
	}
	
	// redireciona para a tela de edição ou inclusão
	public String editarTipoComposicao(){
		return CADASTRO_TIPO_COMPOSICOES;
	}
	
	public void removerTipoComposicao() throws ApplicationBusinessException{
		try{
			prescricaoMedicaFacade.removerAfaTipoComposicoes(consultaTipoComposicoesVOSelecionado.getSeq());
			apresentarMsgNegocio("COMPOS_EXCLUIDO_SUCESSO");
			listaTipoComposicoes();
			
			if(consultaTipoComposicoesVO != null && consultaTipoComposicoesVO.getSeq() != null){
				if(consultaTipoComposicoesVO.getSeq().equals(consultaTipoComposicoesVOSelecionado.getSeq())){
					consultaTipoComposicoesVO.setSeq(null);
					exibirVinculo = Boolean.FALSE;
				}
			}
		}
		catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void filtroPesquisaTiposComposicao(){
		
		tipoComposicoesFiltro = new AfaTipoComposicoes();
		if(codigo != null){
			tipoComposicoesFiltro.setSeq(codigo);
		}
		if(ordem != null){
			tipoComposicoesFiltro.setOrdem(ordem);
		}
		if(indSituacao != null){
			tipoComposicoesFiltro.setIndSituacao(indSituacao);
		}
			
	}
	
	public void limpar(){
		editar = Boolean.FALSE;
		exibirVinculo = Boolean.FALSE;
		exibirBotaoNovo = Boolean.FALSE;
		codigo = null;
		ordem = null;
		indSituacao = null;
		listaTipoComposicoes = null;
		listaGrupoComponente = null;
		afaGrupoComponenteNpt = null;
		consultaTipoComposicoesVO = new ConsultaTipoComposicoesVO();
		tipoComposicoesFiltro = new AfaTipoComposicoes();
	}
	
	public void limparGridsPesquisa() {
		editar = Boolean.FALSE;
		exibirVinculo = Boolean.FALSE;
		listaGrupoComponente = null;
		consultaTipoComposicoesVOSelecionado = null;
		afaGrupoComponenteNpt = null;

	}
	
	/**
	 * Lista de grupos de compoenentes relacionados com a linha selecionada na grid principal
	 * @param seq
	 */
	public void pesquisarListaGrupoComposicao(){
		listaGrupoComponente = prescricaoMedicaFacade.pesquisarListaGrupoComposicoesNPT(consultaTipoComposicoesVO.getSeq());
		setExibirVinculo(Boolean.TRUE);
	}
	
	/**
	 * preenchimento da suggestionBox
	 * @param pesquisa
	 * @return Lista de grupo de componenetes
	 */
	public List<AfaGrupoComponenteNpt> pesquisarGrupoComponentes(final String pesquisa){
		return prescricaoMedicaFacade.pesquisarGrupoComponenteAtivo(pesquisa);
	}
	
	public void editarGrupoComponenteNPTAssociado(){
		editar = Boolean.TRUE;
		ativoGrupo = listaGrupoComponenteSelecionado.getIndSituacao().isAtivo();
		afaGrupoComponenteNpt = prescricaoMedicaFacade.obterGrupoComponente(listaGrupoComponenteSelecionado.getSeq());
	}

	public void removerGrupoComponenteNPT(){
		prescricaoMedicaFacade.removerGrupoComponentesAssociados(listaGrupoComponenteSelecionado);
		pesquisarListaGrupoComposicao();
		apresentarMsgNegocio("GRUPO_COMPOS_EXCLUIDO_SUCESSO");
	}
	
	public void adicionarGrupoComponenteNPT() throws ApplicationBusinessException {
		try{
			if(afaGrupoComponenteNpt != null && afaGrupoComponenteNpt.getSeq() != null){
				prescricaoMedicaFacade.adicionarGrupoComponentesAssociados(afaGrupoComponenteNpt.getSeq(),consultaTipoComposicoesVO.getSeq(), ativoGrupo);
				cancelarGrupoComponenteNPT();
				apresentarMsgNegocio("GRUPO_COMPOS_INSERIDO_SUCESSO");
			}else{
				throw new ApplicationBusinessException(CadastroTipoComposicaoNPTPaginatorControllerExceptionCode.TIPO_COMPOSICAO_MS03);
			}
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	public void alterarGrupoComponenteNPT() throws ApplicationBusinessException{
		try{
			prescricaoMedicaFacade.alterarGrupoComponentesAssociados(afaGrupoComponenteNpt.getSeq(),consultaTipoComposicoesVO.getSeq(), ativoGrupo);
			cancelarGrupoComponenteNPT();
			apresentarMsgNegocio("GRUPO_COMPOS_ALTERADO_SUCESSO");
		}
		catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	public void cancelarGrupoComponenteNPT(){
		editar = Boolean.FALSE;
		ativoGrupo = Boolean.FALSE;
		afaGrupoComponenteNpt = null;
		pesquisarListaGrupoComposicao();
	}
	
	public void acaoLinhaSelecionada(){
		consultaTipoComposicoesVO = consultaTipoComposicoesVOSelecionado;
		pesquisarListaGrupoComposicao();
	}
	
	//GET e SET

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public Short getOrdem() {
		return ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public AfaTipoComposicoes getTipoComposicoesFiltro() {
		return tipoComposicoesFiltro;
	}

	public void setTipoComposicoesFiltro(AfaTipoComposicoes tipoComposicoesFiltro) {
		this.tipoComposicoesFiltro = tipoComposicoesFiltro;
	}

	public List<ConsultaCompoGrupoComponenteVO> getListaGrupoComponente() {
		return listaGrupoComponente;
	}

	public ConsultaTipoComposicoesVO getConsultaTipoComposicoesVO() {
		return consultaTipoComposicoesVO;
	}

	public void setConsultaTipoComposicoesVO(
			ConsultaTipoComposicoesVO consultaTipoComposicoesVO) {
		this.consultaTipoComposicoesVO = consultaTipoComposicoesVO;
	}

	public AfaGrupoComponenteNpt getAfaGrupoComponenteNpt() {
		return afaGrupoComponenteNpt;
	}

	public void setAfaGrupoComponenteNpt(AfaGrupoComponenteNpt afaGrupoComponenteNpt) {
		this.afaGrupoComponenteNpt = afaGrupoComponenteNpt;
	}

	public Boolean getAtivoGrupo() {
		return ativoGrupo;
	}

	public void setAtivoGrupo(Boolean ativoGrupo) {
		this.ativoGrupo = ativoGrupo;
	}

	public ConsultaCompoGrupoComponenteVO getListaGrupoComponenteSelecionado() {
		return listaGrupoComponenteSelecionado;
	}

	public void setListaGrupoComponenteSelecionado(
			ConsultaCompoGrupoComponenteVO listaGrupoComponenteSelecionado) {
		this.listaGrupoComponenteSelecionado = listaGrupoComponenteSelecionado;
	}

	public Boolean isEditar() {
		return editar;
	}

	public void setEditar(Boolean editar) {
		this.editar = editar;
	}

	public ConsultaTipoComposicoesVO getConsultaTipoComposicoesVOSelecionado() {
		return consultaTipoComposicoesVOSelecionado;
	}

	public void setConsultaTipoComposicoesVOSelecionado(
			ConsultaTipoComposicoesVO consultaTipoComposicoesVOSelecionado) {
		this.consultaTipoComposicoesVOSelecionado = consultaTipoComposicoesVOSelecionado;
	}

	public Boolean isExibirVinculo() {
		return exibirVinculo;
	}

	public void setExibirVinculo(Boolean exibirVinculo) {
		this.exibirVinculo = exibirVinculo;
	}

	public List<ConsultaTipoComposicoesVO> getListaTipoComposicoes() {
		return listaTipoComposicoes;
	}

	public void setListaTipoComposicoes(List<ConsultaTipoComposicoesVO> listaTipoComposicoes) {
		this.listaTipoComposicoes = listaTipoComposicoes;
	}

	public AfaCompoGrupoComponente getAfaCompoGrupoComponente() {
		return afaCompoGrupoComponente;
	}

	public void setAfaCompoGrupoComponente(AfaCompoGrupoComponente afaCompoGrupoComponente) {
		this.afaCompoGrupoComponente = afaCompoGrupoComponente;
	}

	public Boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(Boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public Boolean getInciarPesquisa() {
		return inciarPesquisa;
	}

	public void setInciarPesquisa(Boolean inciarPesquisa) {
		this.inciarPesquisa = inciarPesquisa;
	}

	
}