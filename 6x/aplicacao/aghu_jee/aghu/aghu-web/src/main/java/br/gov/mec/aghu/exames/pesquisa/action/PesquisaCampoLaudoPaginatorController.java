package br.gov.mec.aghu.exames.pesquisa.action;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCampoCampoLaudo;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.action.ManterCampoLaudoController;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.vo.CampoLaudoVO;
import br.gov.mec.aghu.model.AelCampoLaudo;
import br.gov.mec.aghu.model.AelGrupoResultadoCaracteristica;
import br.gov.mec.aghu.model.AelGrupoResultadoCodificado;



public class PesquisaCampoLaudoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -1754838063661866869L;

	private static final String MANTER_CAMPO_LAUDO = "exames-manterCampoLaudo";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	// Campos de filtro para pesquisa
	private Integer seq; // Código
	private String nome;
	private DominioSituacao situacao;
	private DominioTipoCampoCampoLaudo tipoCampo;
	private AelGrupoResultadoCodificado grupoResultadoCodificado;	
	private AelGrupoResultadoCaracteristica grupoResultadoCaracteristica;	
	private DominioSimNao permiteDigitacao;
	private DominioSimNao cancelaItemDept;
	private DominioSimNao pertenceContador; 
	private DominioSimNao pertenceAbs;
	
	@Inject
	private ManterCampoLaudoController manterCampoLaudoController;

	@Inject @Paginator
	private DynamicDataModel<CampoLaudoVO> dataModel;
	
	private CampoLaudoVO selecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Suggestion Box de Grupo Resultado Codificado
	 */
	public List<AelGrupoResultadoCodificado> pesquisarGrupoResultadoCodificado(String objPesquisa) {
		return this.examesFacade.pesquisarGrupoResultadoCodificadoPorSeqDescricao(objPesquisa);
	}
	
	/**
	 * Suggestion Box de Grupo Resultado Característica
	 */
	public List<AelGrupoResultadoCaracteristica> pesquisarGrupoResultadoCaracteristica(String objPesquisa) {
		return this.examesFacade.pesquisarGrupoResultadoCaracteristicaPorSeqDescricao(objPesquisa);
	}

	
	/**
	 * Recupera uma instancia com os filtros de pesquisa atualizados
	 */
	private AelCampoLaudo getFiltroPesquisa(){
		
		final AelCampoLaudo filtro = new AelCampoLaudo();

		filtro.setSeq(this.seq);
		filtro.setNome(StringUtils.trim(this.nome));
		filtro.setSituacao(this.situacao);
		filtro.setTipoCampo(this.tipoCampo);
		
		filtro.setGrupoResultadoCodificado(this.grupoResultadoCodificado);
		filtro.setGrupoResultadoCaracteristica(this.grupoResultadoCaracteristica);
		
		filtro.setPermiteDigitacao(this.permiteDigitacao != null ? this.permiteDigitacao.isSim() : null);
		filtro.setCancelaItemDept(this.cancelaItemDept != null ? this.cancelaItemDept.isSim() : null);
		filtro.setPertenceContador(this.pertenceContador != null ? this.pertenceContador.isSim() : null);
		filtro.setPertenceAbs(this.pertenceAbs != null ? this.pertenceAbs.isSim() : null);

		return filtro;
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}
	

	@Override
	public Long recuperarCount() {
		return this.examesFacade.pesquisarCampoLaudoCount(this.getFiltroPesquisa());
	}
	
	@Override
	public List<CampoLaudoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		
		List<AelCampoLaudo> listaCampoLaudo = this.examesFacade.pesquisarCampoLaudo(firstResult, maxResult, orderProperty, asc, this.getFiltroPesquisa());

		List<CampoLaudoVO> resultado = new LinkedList<CampoLaudoVO>();
		
		if(listaCampoLaudo != null && !listaCampoLaudo.isEmpty()){
			
			for (AelCampoLaudo campoLaudo : listaCampoLaudo) {
				
				CampoLaudoVO vo = new CampoLaudoVO(campoLaudo);

				resultado.add(vo);
				
			}
		}
		
		return resultado;
	}
	
	/**
	 * Limpa os filtros da pesquisa principal
	 */
	public void limparPesquisa() {
		dataModel.limparPesquisa();
		
		this.seq = null;
		this.nome = null;
		this.situacao = null;	
		this.tipoCampo = null;
		
		this.grupoResultadoCodificado = null;
		this.grupoResultadoCaracteristica = null;
		
		this.permiteDigitacao = null;
		this.cancelaItemDept = null;
		this.pertenceContador = null;
		this.pertenceAbs = null;
	}

	public void excluir()  {

		try {
			this.cadastrosApoioExamesFacade.removerCampoLaudo(selecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOVER_CAMPO_LAUDO", selecionado.getSeq(), selecionado.getNome());
			
		} catch (BaseListException e) {
			for (Iterator<BaseException> errors = e.iterator(); errors.hasNext();) {
				BaseException aghuNegocioException = (BaseException) errors.next();
				super.apresentarExcecaoNegocio(aghuNegocioException);	
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String inserir(){
		manterCampoLaudoController.setSeq(null);
		return MANTER_CAMPO_LAUDO;
	}
	
	public String editar(){
		manterCampoLaudoController.setSeq(this.seq);
		return MANTER_CAMPO_LAUDO;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public DominioTipoCampoCampoLaudo getTipoCampo() {
		return tipoCampo;
	}

	public void setTipoCampo(DominioTipoCampoCampoLaudo tipoCampo) {
		this.tipoCampo = tipoCampo;
	}

	public AelGrupoResultadoCodificado getGrupoResultadoCodificado() {
		return grupoResultadoCodificado;
	}

	public void setGrupoResultadoCodificado(
			AelGrupoResultadoCodificado grupoResultadoCodificado) {
		this.grupoResultadoCodificado = grupoResultadoCodificado;
	}

	public AelGrupoResultadoCaracteristica getGrupoResultadoCaracteristica() {
		return grupoResultadoCaracteristica;
	}

	public void setGrupoResultadoCaracteristica(
			AelGrupoResultadoCaracteristica grupoResultadoCaracteristica) {
		this.grupoResultadoCaracteristica = grupoResultadoCaracteristica;
	}

	public DominioSimNao getPermiteDigitacao() {
		return permiteDigitacao;
	}

	public void setPermiteDigitacao(DominioSimNao permiteDigitacao) {
		this.permiteDigitacao = permiteDigitacao;
	}

	public DominioSimNao getCancelaItemDept() {
		return cancelaItemDept;
	}

	public void setCancelaItemDept(DominioSimNao cancelaItemDept) {
		this.cancelaItemDept = cancelaItemDept;
	}

	public DominioSimNao getPertenceContador() {
		return pertenceContador;
	}

	public void setPertenceContador(DominioSimNao pertenceContador) {
		this.pertenceContador = pertenceContador;
	}

	public DominioSimNao getPertenceAbs() {
		return pertenceAbs;
	}

	public void setPertenceAbs(DominioSimNao pertenceAbs) {
		this.pertenceAbs = pertenceAbs;
	}

	public DynamicDataModel<CampoLaudoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<CampoLaudoVO> dataModel) {
		this.dataModel = dataModel;
	}

	public CampoLaudoVO getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(CampoLaudoVO selecionado) {
		this.selecionado = selecionado;
	}
}