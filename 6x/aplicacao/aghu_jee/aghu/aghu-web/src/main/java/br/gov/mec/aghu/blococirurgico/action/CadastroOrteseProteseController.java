package br.gov.mec.aghu.blococirurgico.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcMatOrteseProtCirg;
import br.gov.mec.aghu.model.MbcMatOrteseProtCirgId;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class CadastroOrteseProteseController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2277196420275922301L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	/*Cirurgia*/
	private MbcCirurgias cirurgia;
	
	/*Material de Órtese e Pŕotese*/
	private MbcMatOrteseProtCirg materialOrtProt;
	
	/*Grupo de Material*/
	private ScoGrupoMaterial grupoMaterial;
	
	/*Localização*/
	private String localizacao;
	
	/*Prontuario Formatado*/
	private String prontuarioFormatado;
	
	/*Lista de materiais cadastrados*/
	private List<MbcMatOrteseProtCirg> listaOrteseProtese;
	
	/*Unidade do material*/
	private String unidadeMaterial;
	
	private MbcMatOrteseProtCirg itemSelecionado;
	
	private static final String AGENDA_PROCEDIMENTOS = "blococirurgico-agendaProcedimentos";
	
	/**
	 * Codigo da cirurgia, obtido via page parameter.
	 */
	private Integer mbcCirurgiaCodigo;
	
	public void inicio(){
	 
		// mbcCirurgiaCodigo está sendo passado como um valor fixo via page parameter
		if (this.mbcCirurgiaCodigo != null) {
			this.cirurgia = this.blocoCirurgicoFacade.obterCirurgiaPorChavePrimaria(mbcCirurgiaCodigo, new Enum[] {MbcCirurgias.Fields.PACIENTE, MbcCirurgias.Fields.UNIDADE_FUNCIONAL}, new Enum[] {MbcCirurgias.Fields.AGENDA});
					
			atualizarLista(mbcCirurgiaCodigo);
			this.localizacao = this.blocoCirurgicoFacade.obterQuarto(this.cirurgia.getPaciente().getCodigo());
			this.prontuarioFormatado = getProntuarioFormatado(this.cirurgia.getPaciente().getProntuario());
			
			materialOrtProt = new MbcMatOrteseProtCirg();
			materialOrtProt.setMbcCirurgias(cirurgia);
			
		}
	
	}
	
	
	/**
	 * Atualiza a lista de anotações
	 * @param crgSseq
	 */
	private void atualizarLista(Integer crgSeq) {
		this.listaOrteseProtese = this.blocoCirurgicoFacade.pesquisarOrteseProtesePorCirurgia(crgSeq);
	}

	public List<ScoMaterial> pesquisarMaterial(String objParam) throws BaseException{
		final AghParametros pGrMatOrtProt = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_GR_MAT_ORT_PROT);

		grupoMaterial = this.comprasFacade.obterGrupoMaterialPorId(pGrMatOrtProt.getVlrNumerico().intValue());
		
		return this.returnSGWithCount(this.comprasFacade.listarScoMateriaisGrupoAtiva(objParam, grupoMaterial, true, false),pesquisarMaterialCount(objParam));
	}
	
	public Long pesquisarMaterialCount(String objParam) {
		return this.comprasFacade.contarScoMateriaisGrupoAtiva(objParam, grupoMaterial, true);
	}
	
	public void carregarDescricaoUnf() {
		if (this.materialOrtProt.getScoMaterial().getUnidadeMedida() != null){
			unidadeMaterial = this.materialOrtProt.getScoMaterial().getUnidadeMedida().getDescricao();
		} else {
			unidadeMaterial = null;
		}
	}
	
	public void removerDescricaoUnf() {
		unidadeMaterial = null;
	}
	
	private String getProntuarioFormatado(Integer prontuario) {
		return CoreUtil.formataProntuario(prontuario);
	}
	
	public void confirmar(){
		
		if(materialOrtProt.getQtdSolic() == 0 ){
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_QTD_SOLIC_MAIOR_QUE_ZERO");
			return;
		}
				
		try {
			
			if (listaOrteseProtese != null && !listaOrteseProtese.isEmpty()) {
				for (MbcMatOrteseProtCirg mbcMatOrteseProtCirg : listaOrteseProtese) {
					if (mbcMatOrteseProtCirg.getScoMaterial().getCodigo().equals(materialOrtProt.getScoMaterial().getCodigo())) {
						this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_MATERIAL_ORT_PROT_JA_CADASTRADO");
						return;
					}
				}
			}
			this.blocoCirurgicoFacade.persistirMatOrteseProtese(this.materialOrtProt);
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_ORTESE_PROTESE");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		limpar();
	}
	
	public void excluir(Integer matCodigoOrtProt, Integer crgSeqOrtProt){
		final MbcMatOrteseProtCirg matOrtProt = this.blocoCirurgicoFacade
			.obterMbcMatOrteseProtCirgPorChavePrimaria(new MbcMatOrteseProtCirgId(matCodigoOrtProt, crgSeqOrtProt));
		
		try {
			this.blocoCirurgicoFacade.excluirMatOrteseProtese(matOrtProt);
			
			this.apresentarMsgNegocio(Severity.INFO,
			"MENSAGEM_SUCESSO_EXCLUSAO_ORTESE_PROTESE");
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		limpar();
	}
	
	private void limpar() {
		this.removerDescricaoUnf();
		this.materialOrtProt = new MbcMatOrteseProtCirg();
		this.materialOrtProt.setMbcCirurgias(cirurgia);
		atualizarLista(mbcCirurgiaCodigo);	
	}

	public String voltar() {
		return AGENDA_PROCEDIMENTOS;
	}

	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}

	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}

	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}

	public Integer getMbcCirurgiaCodigo() {
		return mbcCirurgiaCodigo;
	}

	public void setMbcCirurgiaCodigo(Integer mbcCirurgiaCodigo) {
		this.mbcCirurgiaCodigo = mbcCirurgiaCodigo;
	}


	public List<MbcMatOrteseProtCirg> getListaOrteseProtese() {
		return listaOrteseProtese;
	}


	public void setListaOrteseProtese(List<MbcMatOrteseProtCirg> listaOrteseProtese) {
		this.listaOrteseProtese = listaOrteseProtese;
	}


	public MbcMatOrteseProtCirg getMaterialOrtProt() {
		return materialOrtProt;
	}


	public void setMaterialOrtProt(MbcMatOrteseProtCirg materialOrtProt) {
		this.materialOrtProt = materialOrtProt;
	}

	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public String getUnidadeMaterial() {
		return unidadeMaterial;
	}

	public void setUnidadeMaterial(String unidadeMaterial) {
		this.unidadeMaterial = unidadeMaterial;
	}

	public MbcMatOrteseProtCirg getItemSelecionado() {
		return itemSelecionado;
	}

	public void setItemSelecionado(MbcMatOrteseProtCirg itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
}
