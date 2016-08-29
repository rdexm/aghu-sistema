package br.gov.mec.aghu.prescricaomedica.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.cadastrosapoio.action.ManterMaterialController;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.VFatConvPlanoGrupoProcedVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatConvGrupoItemProced;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.prescricaomedica.cadastroapoio.action.ManterComponentesSanguineosController;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

public class ProcedimentoRelacionadoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1958486104801963356L;
	
	private static final String JUSTIFICATIVA_MATERIAL = "estoque-manterMaterialJustificativa";
	private static final String MANTER_MATERIAL = "estoque-manterMaterialCRUD";
	
	private Integer phiSeq;
	
	private Integer matCodigo;

	// 34742
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;
	
	@Inject
	private ManterComponentesSanguineosController manterComponentesSanguineosController;
	
	@Inject
	private ManterMaterialController manterMaterialController;
	
	@EJB
	private IPermissionService permissionService;

	@EJB
	private IComprasFacade comprasFacade;

	private VFatConvPlanoGrupoProcedVO tabela;
	private Short cpgGrcSeq;
	private Short cpgCphPhoSeq;

	private VFatConvPlanoGrupoProcedVO convenio;
	private FatItensProcedHospitalar fatItensProcedHospitalar;
	
	// lista da visão em tela
	private List<VFatConvPlanoGrupoProcedVO> listaFatProcedimentoRelacionadosVO;
	// lista a ser persistida
	private List<VFatConvPlanoGrupoProcedVO> listaFatProcedimentoRelacionadosPersistirVO;
	// lista clone para a journal
	private List<FatConvGrupoItemProced> listaClone;

	private VFatConvPlanoGrupoProcedVO vFatConvPlanoGrupoProcedVO;
	private Boolean editandoProcedimentosRelacionados;
	private List<FatProcedHospInternos> listaFatProcedHospInternos;
	private VFatConvPlanoGrupoProcedVO editarFatProcedimentoRelacionadosVO;
	private Boolean temPerfilOrdenadorDespesas;
	private Boolean chamarConfirmarSemVinculos;
	private Boolean chamarCancelar;
	private String voltarPara;
	private Integer serMatriculaJusProcRel;
	private String justificativaProcRel;
	
	
	protected static final String SUS = "SUS";
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio(){
	 

		
		try{
			AghParametros pTabela = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
			this.setCpgCphPhoSeq(pTabela.getVlrNumerico().shortValue());
			List<VFatConvPlanoGrupoProcedVO> listaTabela = this.faturamentoFacade.listarTabelas(this.getCpgCphPhoSeq().toString());
			if (listaTabela != null && !listaTabela.isEmpty()) {
				this.setTabela(listaTabela.get(0));
				this.setCpgGrcSeq(this.getTabela().getGrcSeq());
			}
			if(this.matCodigo != null){
				final ScoMaterial material = this.comprasFacade.obterScoMaterialPorChavePrimaria(this.matCodigo);
				if(material != null){
					this.justificativaProcRel =  material.getJustificativaProcRel();
				}
			}
		} catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}	
		
		List<VFatConvPlanoGrupoProcedVO> listaConvenioInicial = this.listarConvenios(SUS);
		if(!listaConvenioInicial.isEmpty()){
			this.convenio = listaConvenioInicial.get(0);
		}
		
		this.setEditandoProcedimentosRelacionados(Boolean.FALSE);
		this.temPerfilOrdenadorDespesas = this.permissionService
				.usuarioTemPermissao(this.obterLoginUsuarioLogado(), 
						"procedimentoCirurgico", "ordenadorDespesas");
		this.chamarConfirmarSemVinculos = Boolean.FALSE;
		this.chamarCancelar = Boolean.FALSE;
		this.setListaFatProcedHospInternos(new ArrayList<FatProcedHospInternos>());
		this.setListaFatProcedimentoRelacionadosVO(new ArrayList<VFatConvPlanoGrupoProcedVO>());
		this.setListaClone(new ArrayList<FatConvGrupoItemProced>());
		this.setListaFatProcedimentoRelacionadosPersistirVO(new ArrayList<VFatConvPlanoGrupoProcedVO>());
		//#34760
		verificaProcedimentosEditados();
		
	
	}
	
	private void iniciarSuggestions(){
		this.fatItensProcedHospitalar = null;
	}

	// -----------
	// -- 34742 --
	// -----------	

	public List<VFatConvPlanoGrupoProcedVO> listarConvenios(String objPesquisa) {
		return this.returnSGWithCount(this.faturamentoFacade.listarConvenios(objPesquisa, getCpgGrcSeq(), getTabela().getCphPhoSeq()),this.listarConveniosCount(objPesquisa));
	}
	
	public Long listarConveniosCount(String objPesquisa) {
		return this.faturamentoFacade.listarConveniosCount(objPesquisa, getCpgGrcSeq(), getTabela().getCphPhoSeq());
	}
	
	public List<FatItensProcedHospitalar>  listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqOrdenadoPorSeq(String objPesquisa){
		return  this.returnSGWithCount(this.faturamentoFacade.listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqOrdenadoPorSeq(objPesquisa, this.tabela.getCphPhoSeq()),listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(objPesquisa));
	}
	
	public Long listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(String objPesquisa){
		return this.faturamentoFacade.listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(objPesquisa, this.tabela.getCphPhoSeq());
	}
	
	public void excluirProcedimentoRelacionado(VFatConvPlanoGrupoProcedVO vo){
		this.listaFatProcedimentoRelacionadosVO.remove(vo);
		for (int i = 0; i < this.listaFatProcedimentoRelacionadosPersistirVO.size(); i++) {
			if(this.listaFatProcedimentoRelacionadosPersistirVO.get(i).equals(vo)){
				// Se não tiver incluso, remove da lista
				if(this.listaFatProcedimentoRelacionadosPersistirVO.get(i).getOperacao().equals(DominioOperacoesJournal.INS)){
					this.listaFatProcedimentoRelacionadosPersistirVO.remove(vo);
				// caso esteja incluso, adiciona o DEL para remover depois	
				}else{
					this.listaFatProcedimentoRelacionadosPersistirVO.get(i).setOperacao(DominioOperacoesJournal.DEL);
				}
			}
		}
	}
	
	public void cancelarProcedimentoRelacionado(){
		this.editandoProcedimentosRelacionados = Boolean.FALSE;
		iniciarSuggestions();		
	}
	
	public String cancelar(){
		this.manterMaterialController.setRetornouProcedimentos(false);
		return voltarPara;
	}

	public String gravarProcedimentosRelacionados(){
		if(voltarPara.equals(MANTER_MATERIAL)){
			if(listaFatProcedimentoRelacionadosPersistirVO != null && listaFatProcedimentoRelacionadosPersistirVO.size() > 0){
				this.manterMaterialController.setProcessosRelacionados(listaFatProcedimentoRelacionadosPersistirVO);
			}
			if(listaClone != null && listaClone.size() > 0){
				this.manterMaterialController.setListaClones(listaClone);
			}
			this.manterMaterialController.setRetornouProcedimentos(true);
		}
		return voltarPara;
	}

	public String justificar(){
		return JUSTIFICATIVA_MATERIAL;
	}
	
	public Boolean verificaQuantidade(){
		if(voltarPara.equals(MANTER_MATERIAL) && listaFatProcedimentoRelacionadosVO.size() == 1){
			return true;
		}
		return false;
	}
	
	public void editarProcedimentoRelacionado(VFatConvPlanoGrupoProcedVO vo){
		this.editandoProcedimentosRelacionados = Boolean.TRUE;
		this.setvFatConvPlanoGrupoProcedVO(vo);
		this.fatItensProcedHospitalar = vo.getFatItensProcedHospitalar();
	}
	
	public void alterarProcedimentoRelacionado(){
		this.editandoProcedimentosRelacionados = Boolean.FALSE;
		if(fatItensProcedHospitalar != null){
			vFatConvPlanoGrupoProcedVO.setFatItensProcedHospitalar(fatItensProcedHospitalar);
		}
		iniciarSuggestions();		
	}
	
	public void adicionarProcedimentosRelacionados(){
		if(fatItensProcedHospitalar != null && convenio != null){
			try {
				cadastrosApoioExamesFacade.adicionarProcedimentosRelacionados(getListaFatProcedimentoRelacionadosVO(), convenio, fatItensProcedHospitalar, this.cpgGrcSeq);
				//# 34760
				for (VFatConvPlanoGrupoProcedVO item : listaFatProcedimentoRelacionadosVO) {
					if(!listaFatProcedimentoRelacionadosPersistirVO.contains(item)){
						listaFatProcedimentoRelacionadosPersistirVO.add(item);
					}
				}
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			iniciarSuggestions();		
		}
	}
	
	private void verificaProcedimentosEditados() {
		if(voltarPara.equals(MANTER_MATERIAL)){
				if(phiSeq != null){
					List<FatConvGrupoItemProced> listaProcedRelacionados = faturamentoFacade.listarFatConvGrupoItensProcedPorPhi(null, null, null, null, this.getCpgCphPhoSeq(), this.getCpgGrcSeq(), phiSeq);
					listaClone.addAll(listaProcedRelacionados);
					
					// Popula view
					listaFatProcedimentoRelacionadosVO = new ArrayList<VFatConvPlanoGrupoProcedVO>(0);
					listaFatProcedimentoRelacionadosPersistirVO = new ArrayList<VFatConvPlanoGrupoProcedVO>(0);
					for (FatConvGrupoItemProced fatConvGrupoItemProced : listaClone) {
						List<FatProcedHospInternos> lista = faturamentoFacade.pesquisarProcedimentosInternosPeloMatCodigo(this.matCodigo);
						FatProcedHospInternos PHI = lista.get(0);
						PHI.getDescricao();
						
						fatConvGrupoItemProced.getConvenioSaudePlano();
						
						// POPULAR VIEW COM AS DESCRICOES
						VFatConvPlanoGrupoProcedVO item = new VFatConvPlanoGrupoProcedVO();
						item.setFatItensProcedHospitalar(fatConvGrupoItemProced.getItemProcedHospitalar());
						item.setCnvDescricao(fatConvGrupoItemProced.getConvenioSaudePlano().getConvenioSaude().getDescricao());
						item.setCphCspSeq(fatConvGrupoItemProced.getConvenioSaudePlano().getId().getSeq());
						item.setCspDescricao(fatConvGrupoItemProced.getConvenioSaudePlano().getDescricao());
						
						item.setCphCspCnvCodigo(fatConvGrupoItemProced.getId().getCpgCphCspCnvCodigo());
						item.setCphCspSeq(fatConvGrupoItemProced.getId().getCpgCphCspSeq());
						item.setCphPhoSeq(fatConvGrupoItemProced.getId().getCpgCphPhoSeq());
						item.setGrcSeq(fatConvGrupoItemProced.getId().getCpgGrcSeq());
						item.setIphSeq(fatConvGrupoItemProced.getId().getIphSeq());
						item.setIphPhoSeq(fatConvGrupoItemProced.getId().getIphPhoSeq());
						item.setPhiSeq(fatConvGrupoItemProced.getId().getPhiSeq());
						item.setOperacao(DominioOperacoesJournal.UPD);
						// salva nas 2 listas
						listaFatProcedimentoRelacionadosVO.add(item);
						listaFatProcedimentoRelacionadosPersistirVO.add(item);
					}
				}
		}
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}
	
	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public Integer getMatCodigo() {
		return matCodigo;
	}
	
	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	public IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	public ICadastrosApoioExamesFacade getCadastrosApoioExamesFacade() {
		return cadastrosApoioExamesFacade;
	}

	public ManterComponentesSanguineosController getManterComponentesSanguineosController() {
		return manterComponentesSanguineosController;
	}

	public ManterMaterialController getManterMaterialController() {
		return manterMaterialController;
	}

	public IPermissionService getPermissionService() {
		return permissionService;
	}

	public IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	public VFatConvPlanoGrupoProcedVO getTabela() {
		return tabela;
	}

	public Short getCpgGrcSeq() {
		return cpgGrcSeq;
	}

	public Short getCpgCphPhoSeq() {
		return cpgCphPhoSeq;
	}

	public VFatConvPlanoGrupoProcedVO getConvenio() {
		return convenio;
	}

	public void setConvenio(VFatConvPlanoGrupoProcedVO convenio) {
		this.convenio = convenio;
	}

	public FatItensProcedHospitalar getFatItensProcedHospitalar() {
		return fatItensProcedHospitalar;
	}

	public List<VFatConvPlanoGrupoProcedVO> getListaFatProcedimentoRelacionadosVO() {
		return listaFatProcedimentoRelacionadosVO;
	}

	public List<VFatConvPlanoGrupoProcedVO> getListaFatProcedimentoRelacionadosPersistirVO() {
		return listaFatProcedimentoRelacionadosPersistirVO;
	}

	public List<FatConvGrupoItemProced> getListaClone() {
		return listaClone;
	}

	public VFatConvPlanoGrupoProcedVO getvFatConvPlanoGrupoProcedVO() {
		return vFatConvPlanoGrupoProcedVO;
	}

	public Boolean getEditandoProcedimentosRelacionados() {
		return editandoProcedimentosRelacionados;
	}

	public List<FatProcedHospInternos> getListaFatProcedHospInternos() {
		return listaFatProcedHospInternos;
	}

	public VFatConvPlanoGrupoProcedVO getEditarFatProcedimentoRelacionadosVO() {
		return editarFatProcedimentoRelacionadosVO;
	}

	public Boolean getTemPerfilOrdenadorDespesas() {
		return temPerfilOrdenadorDespesas;
	}

	public Boolean getChamarConfirmarSemVinculos() {
		return chamarConfirmarSemVinculos;
	}

	public Boolean getChamarCancelar() {
		return chamarCancelar;
	}

	public String getVoltarPara() {
		return voltarPara;
	}
	
	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getSerMatriculaJusProcRel() {
		return serMatriculaJusProcRel;
	}

	public void setSerMatriculaJusProcRel(Integer serMatriculaJusProcRel) {
		this.serMatriculaJusProcRel = serMatriculaJusProcRel;
	}
	
	public String getJustificativaProcRel() {
		return justificativaProcRel;
	}
	
	public void setJustificativaProcRel(String justificativaProcRel) {
		this.justificativaProcRel = justificativaProcRel;
	}

	public static String getSus() {
		return SUS;
	}

	public void setCpgGrcSeq(Short cpgGrcSeq) {
		this.cpgGrcSeq = cpgGrcSeq;
	}

	public void setCpgCphPhoSeq(Short cpgCphPhoSeq) {
		this.cpgCphPhoSeq = cpgCphPhoSeq;
	}

	public void setTabela(VFatConvPlanoGrupoProcedVO tabela) {
		this.tabela = tabela;
	}

	public void setEditandoProcedimentosRelacionados(
			Boolean editandoProcedimentosRelacionados) {
		this.editandoProcedimentosRelacionados = editandoProcedimentosRelacionados;
	}

	public void setListaFatProcedHospInternos(
			List<FatProcedHospInternos> listaFatProcedHospInternos) {
		this.listaFatProcedHospInternos = listaFatProcedHospInternos;
	}

	public void setEditarFatProcedimentoRelacionadosVO(
			VFatConvPlanoGrupoProcedVO editarFatProcedimentoRelacionadosVO) {
		this.editarFatProcedimentoRelacionadosVO = editarFatProcedimentoRelacionadosVO;
	}

	public void setListaFatProcedimentoRelacionadosVO(
			List<VFatConvPlanoGrupoProcedVO> listaFatProcedimentoRelacionadosVO) {
		this.listaFatProcedimentoRelacionadosVO = listaFatProcedimentoRelacionadosVO;
	}

	public void setListaFatProcedimentoRelacionadosPersistirVO(
			List<VFatConvPlanoGrupoProcedVO> listaFatProcedimentoRelacionadosPersistirVO) {
		this.listaFatProcedimentoRelacionadosPersistirVO = listaFatProcedimentoRelacionadosPersistirVO;
	}

	public void setListaClone(List<FatConvGrupoItemProced> listaClone) {
		this.listaClone = listaClone;
	}

	public void setvFatConvPlanoGrupoProcedVO(
			VFatConvPlanoGrupoProcedVO vFatConvPlanoGrupoProcedVO) {
		this.vFatConvPlanoGrupoProcedVO = vFatConvPlanoGrupoProcedVO;
	}

	protected static String getManterMaterial() {
		return MANTER_MATERIAL;
	}

	protected static String getJustificativaMaterial() {
		return JUSTIFICATIVA_MATERIAL;
	}

	public void setFatItensProcedHospitalar(
			FatItensProcedHospitalar fatItensProcedHospitalar) {
		this.fatItensProcedHospitalar = fatItensProcedHospitalar;
	}


}
