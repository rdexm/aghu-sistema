package br.gov.mec.aghu.faturamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AbsComponenteSanguineo;
import br.gov.mec.aghu.model.AbsProcedHemoterapico;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ConsultarPhiPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1869976067409724412L;
	
	@Inject @Paginator
	private DynamicDataModel<FatProcedHospInternos> dataModel;

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private RelacionarCidComProcedimentoHospitalarInternoController relacionarCidComPHIController;

	private FatProcedHospInternos phi;
	private FatProcedHospInternos procedHospInternos;
	private ScoMaterial material; 
	private MbcProcedimentoCirurgicos procedimentoCirurgico; 
	private MpmProcedEspecialDiversos procedEspecialDiverso;
	private AbsComponenteSanguineo componenteSanguineo; 
	private AbsProcedHemoterapico procedHemoterapico;
	private AelExamesMaterialAnalise exameMaterialAnalise;
	private boolean indAtivo;
	
	
	private boolean exibirListagem = false; 
	
	//usada para auxiliar no redirecionamento
	//botao fechar
	private String from;
	
	public enum ConsultarPhiPaginatorControllerExceptionCode implements BusinessExceptionCode {
		MSG_ALTERADO_SUCESSO
	}
	
	@PostConstruct
	public void inicio() {
		begin(conversation, true);
		this.indAtivo = true;
	}
	
	public void pesquisar() {
		this.exibirListagem = true;
		this.dataModel.reiniciarPaginator();		
	}

	public void limparPesquisa() {
		this.procedHospInternos = null;
		this.material = null; 
		this.procedimentoCirurgico = null; 
		this.procedEspecialDiverso = null;
		this.componenteSanguineo = null; 
		this.procedHemoterapico = null;
		this.exameMaterialAnalise = null;
		this.indAtivo = true;		
		this.phi = null;
		this.exibirListagem = false;
		this.dataModel.setPesquisaAtiva(false);
	}
	
	public void editar(FatProcedHospInternos phi){
		this.phi = phi;		
	}

	public void atualizar() {
		FatProcedHospInternos phiBanco = faturamentoFacade.obterProcedimentoHospitalarInterno(phi.getSeq());
		if(DominioSituacao.I.equals(phi.getSituacao())){
			phi.setSituacao(DominioSituacao.A);
			phiBanco.setSituacao(DominioSituacao.A);
			
		}else{
			phi.setSituacao(DominioSituacao.I);
			phiBanco.setSituacao(DominioSituacao.I);
		}
		try{			
			this.faturamentoFacade.atualizarFatProcedHospInternos(phiBanco);
			
			this.apresentarMsgNegocio(Severity.INFO,
					ConsultarPhiPaginatorControllerExceptionCode.MSG_ALTERADO_SUCESSO.toString());
			
		}catch (ApplicationBusinessException  e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public String voltar() {
		this.limparPesquisa();
		this.dataModel.limparPesquisa();
		return "faturamento-manterItemPrincipal";
	}
	
	public boolean definirIconeColunaAcao(Integer phiSeq){
		boolean retorno = false;
		List<VFatAssociacaoProcedimento> lista = null;
		try{
			lista = this.faturamentoFacade.listarVFatAssociacaoProcedimentoPorProcedHospInt(phiSeq); 
		}catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
		
		if(lista != null && !lista.isEmpty()){
			retorno = true;
		}
		
		return retorno;
	}

	//Integer seq, DominioSituacao situacao, Integer matCodigo, Integer pciSeq, 
	//Short pedSeq, String csaCodigo, String pheCodigo, String emaExaSigla, Integer emaManSeq
	@Override
	public Long recuperarCount() {
		return faturamentoFacade.listarPhisCount(procedHospInternos != null ? procedHospInternos.getSeq() : null, 
				indAtivo ? DominioSituacao.A : DominioSituacao.I, 
				material != null ? material.getCodigo() : null,
				procedimentoCirurgico != null ? procedimentoCirurgico.getSeq() : null,
				procedEspecialDiverso != null ? procedEspecialDiverso.getSeq() : null,
				componenteSanguineo != null ? componenteSanguineo.getCodigo() : null,
				procedHemoterapico != null ? procedHemoterapico.getCodigo() : null,
				exameMaterialAnalise != null ? exameMaterialAnalise.getId().getExaSigla() : null,
				exameMaterialAnalise != null ? exameMaterialAnalise.getId().getManSeq() : null
				);
	}

	@Override
	@SuppressWarnings({ "PMD.NPathComplexity", "unchecked" })
	public List<FatProcedHospInternos> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {		
		
		List<FatProcedHospInternos> lista = faturamentoFacade.listarPhis(firstResult, maxResult, orderProperty, asc,
				procedHospInternos != null ? procedHospInternos.getSeq() : null, 
						indAtivo ? DominioSituacao.A : DominioSituacao.I, 
						material != null ? material.getCodigo() : null,
						procedimentoCirurgico != null ? procedimentoCirurgico.getSeq() : null,
						procedEspecialDiverso != null ? procedEspecialDiverso.getSeq() : null,
						componenteSanguineo != null ? componenteSanguineo.getCodigo() : null,
						procedHemoterapico != null ? procedHemoterapico.getCodigo() : null,
						exameMaterialAnalise != null ? exameMaterialAnalise.getId().getExaSigla() : null,
						exameMaterialAnalise != null ? exameMaterialAnalise.getId().getManSeq() : null
						);
		if(lista != null){
			for (FatProcedHospInternos element : lista) {
				List<VFatAssociacaoProcedimento> listaAux = null;
				try{
					listaAux = this.faturamentoFacade.listarVFatAssociacaoProcedimentoPorProcedHospInt(element.getSeq()); 
				}catch (ApplicationBusinessException e) {
					this.apresentarExcecaoNegocio(e);
				}
				
				if(listaAux != null && !listaAux.isEmpty()){
					element.setPhiRelacionadoComCid(true);
				}else{
					element.setPhiRelacionadoComCid(false);
				}
			}
		}
		return lista;
		
	}
	
	public String relacionarCIDComPHI(FatProcedHospInternos phi) {
		this.phi = phi;
		this.relacionarCidComPHIController.setPhiSeq(this.phi.getSeq());
		this.relacionarCidComPHIController.iniciar();
		return "faturamento-relacionarCidComProcedimentoHospitalarInterno";
	}

	//###################//
	//### SUGGESTIONS ###//
	//###################//
	
	//Suggestion phis
	public List<FatProcedHospInternos> listarPhisSuggestion(String objPesquisa) {
		return this.returnSGWithCount(this.faturamentoFacade.listarPhis(objPesquisa),listarPhisSuggestionCount(objPesquisa));
	}
	
	public Long listarPhisSuggestionCount(String objPesquisa) {
		return this.faturamentoFacade.listarPhisCount(objPesquisa);
	}
	
	//Suggestion materiais
	public List<ScoMaterial> listarScoMateriais(String objPesquisa){
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriais(objPesquisa, null),listarScoMateriaisCount(objPesquisa));
	}
	
	public Long listarScoMateriaisCount(String objPesquisa){
		return this.comprasFacade.listarScoMateriaisCount(objPesquisa);
	}
	
	//Suggestion para MbcProcedimentoCirurgicos,
	public List<MbcProcedimentoCirurgicos> listarMbcProcedimentoCirurgicos(String objPesquisa){
		return this.returnSGWithCount(this.blocoCirurgicoFacade.listarMbcProcedimentoCirurgicos(objPesquisa),listarMbcProcedimentoCirurgicosCount(objPesquisa));
	}
	
	public Long listarMbcProcedimentoCirurgicosCount(String objPesquisa){
		return this.blocoCirurgicoFacade.listarMbcProcedimentoCirurgicosCount(objPesquisa);
	}
	
	//Suggestion para  MpmProcedEspecialDiversos,
	public List<MpmProcedEspecialDiversos> listarMpmProcedEspecialDiversos(String objPesquisa){
		return this.returnSGWithCount(this.prescricaoMedicaFacade.listarMpmProcedEspecialDiversos(objPesquisa),listarMpmProcedEspecialDiversosCount(objPesquisa));
	}

	public Long listarMpmProcedEspecialDiversosCount(String objPesquisa){
		return this.prescricaoMedicaFacade.listarMpmProcedEspecialDiversosCount(objPesquisa);
	}

	//Suggestion para  AbsComponenteSanguineos,
	public List<AbsComponenteSanguineo> listarAbsComponenteSanguineos(String objPesquisa){
		return this.returnSGWithCount(this.bancoDeSangueFacade.listarAbsComponenteSanguineos(objPesquisa),listarAbsComponenteSanguineosCount(objPesquisa));
	}
	
	public Long listarAbsComponenteSanguineosCount(String objPesquisa){
		return this.bancoDeSangueFacade.listarAbsComponenteSanguineosCount(objPesquisa);
	}
	
	//Suggestion para  AbsProcedHemoterapicos 
	public List<AbsProcedHemoterapico> listarAbsProcedHemoterapicos(String objPesquisa){
		return this.returnSGWithCount(this.bancoDeSangueFacade.listarAbsProcedHemoterapicos(objPesquisa),listarAbsProcedHemoterapicosCount(objPesquisa));
	}
	
	public Long listarAbsProcedHemoterapicosCount(String objPesquisa){
		return this.bancoDeSangueFacade.listarAbsProcedHemoterapicoCount(objPesquisa);
	}
	
	//Suggestion para  AelExames,
	public List<AelExamesMaterialAnalise> listarExamesMaterialAnalise(String objPesquisa){
		return this.returnSGWithCount(this.examesFacade.listarExamesMaterialAnalise(objPesquisa),listarExamesMaterialAnaliseCount(objPesquisa));
	}
	
	public Long listarExamesMaterialAnaliseCount(String objPesquisa){
		return this.examesFacade.listarExamesMaterialAnaliseCount(objPesquisa);
	}	

	//#########################
	//## GETTERS and SETTERs ##
	//#########################	

	public FatProcedHospInternos getProcedHospInternos() {
		return procedHospInternos;
	}

	public void setProcedHospInternos(FatProcedHospInternos procedHospInternos) {
		this.procedHospInternos = procedHospInternos;
	}

	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	public MbcProcedimentoCirurgicos getProcedimentoCirurgico() {
		return procedimentoCirurgico;
	}

	public void setProcedimentoCirurgico(
			MbcProcedimentoCirurgicos procedimentoCirurgico) {
		this.procedimentoCirurgico = procedimentoCirurgico;
	}

	public MpmProcedEspecialDiversos getProcedEspecialDiverso() {
		return procedEspecialDiverso;
	}

	public void setProcedEspecialDiverso(
			MpmProcedEspecialDiversos procedEspecialDiverso) {
		this.procedEspecialDiverso = procedEspecialDiverso;
	}

	public AbsComponenteSanguineo getComponenteSanguineo() {
		return componenteSanguineo;
	}

	public void setComponenteSanguineo(AbsComponenteSanguineo componenteSanguineo) {
		this.componenteSanguineo = componenteSanguineo;
	}

	public AbsProcedHemoterapico getProcedHemoterapico() {
		return procedHemoterapico;
	}

	public void setProcedHemoterapico(AbsProcedHemoterapico procedHemoterapico) {
		this.procedHemoterapico = procedHemoterapico;
	}

	public AelExamesMaterialAnalise getExameMaterialAnalise() {
		return exameMaterialAnalise;
	}

	public void setExameMaterialAnalise(
			AelExamesMaterialAnalise exameMaterialAnalise) {
		this.exameMaterialAnalise = exameMaterialAnalise;
	}

	public boolean isIndAtivo() {
		return indAtivo;
	}

	public void setIndAtivo(boolean indAtivo) {
		this.indAtivo = indAtivo;
	}

	public boolean isExibirListagem() {
		return exibirListagem;
	}

	public void setExibirListagem(boolean exibirListagem) {
		this.exibirListagem = exibirListagem;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public FatProcedHospInternos getPhi() {
		return phi;
	}

	public void setPhi(FatProcedHospInternos phi) {
		this.phi = phi;
	}

	public DynamicDataModel<FatProcedHospInternos> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatProcedHospInternos> dataModel) {
		this.dataModel = dataModel;
	}
}
