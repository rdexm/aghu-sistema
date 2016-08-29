package br.gov.mec.aghu.blococirurgico.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.opmes.business.IBlocoCirurgicoOpmesFacade;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoCirurgicaMateriaisConsumidosVO;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioDescricaoCirurgiaController;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;


public class DescricaoCirurgicaMateriaisConsumidosController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -6704014358410164918L;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IBlocoCirurgicoOpmesFacade blocoCirurgicoOpmesFacade;
	
	private List<DescricaoCirurgicaMateriaisConsumidosVO> listaMateriaisConsumidos;
	
	private Integer cirurgiaSeq;
	private DescricaoCirurgicaMateriaisConsumidosVO itemMaterialConsumido;
	private String justificativa = "";
	private String justificativaExibir;
	private Boolean exibeAtencao = Boolean.FALSE;
	
	@Inject
	private	RelatorioDescricaoCirurgiaController relatorioDescricaoCirurgiaController;
	
	public void iniciar() {
		this.setListaMateriaisConsumidos(pesquisarMateriaisConsumidos());
		justificativa = "";
		relatorioDescricaoCirurgiaController.inicio();
	}

	public List<DescricaoCirurgicaMateriaisConsumidosVO> pesquisarMateriaisConsumidos(){
		this.listaMateriaisConsumidos = this.blocoCirurgicoFacade.pesquisarMateriaisConsumidos(this.cirurgiaSeq);
		
		if(!this.listaMateriaisConsumidos.isEmpty()){
			String descJustif = this.blocoCirurgicoOpmesFacade.montarJustificativaMateriaisConsumidos(this.listaMateriaisConsumidos.get(0).getSeqRequisicaoOpme());
			this.setJustificativaExibir(descJustif);
		}
		
		return this.listaMateriaisConsumidos;
	}
	
	public void concluirJustificativa(){
		
		if(!this.listaMateriaisConsumidos.isEmpty()){
			Short seqRequisicao = this.listaMateriaisConsumidos.get(0).getSeqRequisicaoOpme();
			this.blocoCirurgicoOpmesFacade.concluirJustificativa(seqRequisicao, this.justificativa);
			closeDialog("modalJustificativaAbaMateriaisConsumidosWG");
			relatorioDescricaoCirurgiaController.inicio();
		}
	}
	
	public void concluirMateriaisConsumidos(){
		Boolean concluir = Boolean.TRUE;
		if(!this.listaMateriaisConsumidos.isEmpty()){
			this.blocoCirurgicoOpmesFacade.validarConcluirMateriaisConsumidos(this.listaMateriaisConsumidos, this.justificativa);
			for (DescricaoCirurgicaMateriaisConsumidosVO matConsumido : this.listaMateriaisConsumidos) {
				if(matConsumido.getIncompativel() && justificativa.isEmpty()){
					openDialog("modalAtencaoWG");
					concluir = Boolean.FALSE;
					break;
				} else {
					concluir = Boolean.TRUE;
					closeDialog("modalAtencaoWG");
				}
			}
			if(concluir){
				concluirJustificativa();
				this.blocoCirurgicoOpmesFacade.concluirMateriaisConsumidos(this.listaMateriaisConsumidos);
				apresentarMsgNegocio(Severity.INFO,"SUCESSO_OPMES_CONCLUIDA");
			}
			relatorioDescricaoCirurgiaController.inicio();
		}
	}
	
	public void validaQtdeUtilizada(DescricaoCirurgicaMateriaisConsumidosVO itemMaterialConsumido){
		this.blocoCirurgicoOpmesFacade.validaQtdeUtilizada(itemMaterialConsumido);
		relatorioDescricaoCirurgiaController.inicio();
	}
	
	public List<DescricaoCirurgicaMateriaisConsumidosVO> getListaMateriaisConsumidos() {
		return listaMateriaisConsumidos;
	}

	public void setListaMateriaisConsumidos(List<DescricaoCirurgicaMateriaisConsumidosVO> listaMateriaisConsumidos) {
		this.listaMateriaisConsumidos = listaMateriaisConsumidos;
	}

	public Integer getCirurgiaSeq() {
		return cirurgiaSeq;
	}

	public void setCirurgiaSeq(Integer cirurgiaSeq) {
		this.cirurgiaSeq = cirurgiaSeq;
	}

	public DescricaoCirurgicaMateriaisConsumidosVO getItemMaterialConsumido() {
		return itemMaterialConsumido;
	}

	public void setItemMaterialConsumido(DescricaoCirurgicaMateriaisConsumidosVO itemMaterialConsumido) {
		this.itemMaterialConsumido = itemMaterialConsumido;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public Boolean getExibeAtencao() {
		return exibeAtencao;
	}

	public void setExibeAtencao(Boolean exibeAtencao) {
		this.exibeAtencao = exibeAtencao;
	}
	
	public String getJustificativaExibir() {
		return justificativaExibir;
	}

	public void setJustificativaExibir(String justificativaExibir) {
		this.justificativaExibir = justificativaExibir;
	}
}
