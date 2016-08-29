package br.gov.mec.aghu.exames.patologia.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelCestoPatologia;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelLaminaAps;
import br.gov.mec.aghu.model.AelMaterialAp;
import br.gov.mec.aghu.model.AelTextoPadraoColoracs;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.StringUtil;


public class LaminaLaudoUnicoController extends ActionController  {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}


	private static final long serialVersionUID = 8053694885268476473L;

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;
	
	@Inject
	private LaudoUnicoController laudoUnicoController;
	
	@Inject
	private DynamicDataModel<AelLaminaAps> dataModel;
	
	private Date dtLamina;
	private AelCestoPatologia cesto;
	private Integer nroCapsulas;
	private String nroFragmentos;
	private String dsLamina;
	private String observacao;
	private AelTextoPadraoColoracs textoPadraoColoracs;
	
	private AelLaminaAps laminaEmMemoria;
	private AelLaminaAps laminaEmEdicao;
	private List<AelLaminaAps> laminasEmMemoria;
	private AelLaminaAps laminasEmMemoriaSelecionado;
	private List<AelLaminaAps> laminasParaRemover;
	private List<AelLaminaAps> listaMateriaisGeracaoCapsulas;

	private AelMaterialAp material;
	private AelExameAp exameAp;
	
	private Long luxSeq;
	private int seqExcluir;
	private Boolean editando = false;
	
	private Boolean geracaoCapOk = false;
	private List<AelLaminaAps> laminas;
	private boolean laudoAntigo;
	
	public void inicio(Long seq) {
		this.luxSeq = seq;
		if(laudoAntigo){
			laminas = this.examesPatologiaFacade.obterListaLaminasPeloExameApSeq(seq);
		}
		laminasEmMemoria = this.examesPatologiaFacade.obterListaLaminasPeloExameApSeq(seq);
		geracaoCapOk = laminasEmMemoria != null && !laminasEmMemoria.isEmpty();
		laminasParaRemover = new ArrayList<AelLaminaAps>();
		this.limpar();
		avaliaUnicoMaterial();
		//exameAp = this.examesPatologiaFacade.obterAelExameApPorChavePrimaria(luxSeq);
		exameAp = this.examesPatologiaFacade.obterAelExameApPorSeq(luxSeq);
		nroCapsulas = 1;
		
		verificarGeracaoCapsulas();
	}
	
	private void verificarGeracaoCapsulas() {
		if(laminasEmMemoria == null || laminasEmMemoria.isEmpty()) {
			//HABILITAR GERAÇÃO AUTOMÁTICA
			listaMateriaisGeracaoCapsulas = new ArrayList<AelLaminaAps>();
			List<AelMaterialAp> materiais = this.examesPatologiaFacade.obterAelMaterialApPorAelExameAps(this.luxSeq);
			if(materiais != null && !materiais.isEmpty()) {
				for(AelMaterialAp mat : materiais) {
					AelLaminaAps lamina = new AelLaminaAps();
					lamina.setAelMaterialAp(mat);
					listaMateriaisGeracaoCapsulas.add(lamina);
				}
			}
		}
	}
	
	public void gerarCapsulas() {
		
		if(listaMateriaisGeracaoCapsulas != null && !listaMateriaisGeracaoCapsulas.isEmpty()) {
			for(AelLaminaAps lmna : listaMateriaisGeracaoCapsulas) {
				
				if (lmna.getCestoPatologia() == null) {
					laminasEmMemoria.clear();
					apresentarMsgNegocio(Severity.ERROR, "GERACAO_CAPSULAS_CAMPO_OBRIGATORIO");
					return;
				} else {
					cesto = lmna.getCestoPatologia();
				}
				
				if (!StringUtils.isNotBlank(lmna.getNumeroCapsula())) {
					laminasEmMemoria.clear();
					apresentarMsgNegocio(Severity.ERROR, "GERACAO_CAPSULAS_CAMPO_OBRIGATORIO");
					return;
				} else {
					nroCapsulas = Integer.valueOf(lmna.getNumeroCapsula());
				}
				
				if (!StringUtils.isNotBlank(lmna.getNumeroFragmentos())) {
					laminasEmMemoria.clear();
					apresentarMsgNegocio(Severity.ERROR, "GERACAO_CAPSULAS_CAMPO_OBRIGATORIO");
					return;
				} else {
					nroFragmentos = lmna.getNumeroFragmentos();
				}
				
				observacao = lmna.getObservacao();
				material = lmna.getAelMaterialAp();
				gravaLaminasEmMEmoria();
			}
			this.setGeracaoCapOk(true);
			apresentarMsgNegocio(Severity.INFO, "MSG_CAPSULAS_CRIADAS_INDICE_BLOCOS");
			
			listaMateriaisGeracaoCapsulas = null;
			this.laudoUnicoController.setDadosAlterados(true);
		}
	}
	
	private void avaliaUnicoMaterial(){
		material = null;
		if(pesquisaMateriaisCapsulaCount(null) == 1){
			material = pesquisaMateriaisCapsula(null).get(0);
		}
	}
	
	public void limpar() {
		dtLamina = new Date();
		cesto = null;
		nroCapsulas = 1;
		nroFragmentos = null;
		dsLamina = null;
		textoPadraoColoracs = null;
		observacao = null;
		editando = false;
	}
	
	public void alterarLaminaEmMemoria(){
		laminaEmEdicao = populaLamina();
		laminasEmMemoria.remove(Integer.valueOf(laminaEmEdicao.getNumeroCapsula()) -1);
		laminasEmMemoria.add(Integer.valueOf(laminaEmEdicao.getNumeroCapsula()) -1, laminaEmEdicao);
		ordenaLaminas();
		alteraValorCapsulas();
		//this.limpar();
	}
	
	public void gravaLaminasEmMEmoria(){
		preparaLaminas();
		ordenaLaminas();
		alteraValorCapsulas();
		this.setGeracaoCapOk(true);
		this.laudoUnicoController.setDadosAlterados(true);
		//this.limpar();
		//this.apresentarMsgNegocio(Severity.INFO, "MSG_CAPSULAS_CRIADAS_INDICE_BLOCOS");		
	}
	
	private void preparaLaminas(){
		for (int i = 0; i < nroCapsulas; i++) {
			criarListaLaminas();
		}
	}
	
	private void criarListaLaminas() {
		laminasEmMemoria.add(populaLamina());
	}

	private AelLaminaAps populaLamina() {
		laminaEmMemoria = new AelLaminaAps();
		laminaEmMemoria.setAelExameAp(exameAp);
		laminaEmMemoria.setDthrLamina(dtLamina);
		laminaEmMemoria.setCestoPatologia(cesto);
		laminaEmMemoria.setCesto(cesto.getDescricao());
		laminaEmMemoria.setNumeroFragmentos(nroFragmentos);
		laminaEmMemoria.setDescricao(dsLamina);
		laminaEmMemoria.setObservacao(observacao);
		laminaEmMemoria.setAelMaterialAp(material);
		laminaEmMemoria.setAelTextoPadraoColoracs(textoPadraoColoracs);
		laminaEmMemoria.setNumeroCapsula(nroCapsulas.toString());
		return laminaEmMemoria;
	}
	
	@SuppressWarnings("unchecked")
	private void ordenaLaminas(){
		final BeanComparator ordemSorter = new BeanComparator("aelMaterialAp.ordem", new NullComparator(false));
		Collections.sort(laminasEmMemoria, ordemSorter);
	}
	
	private void alteraValorCapsulas(){
		Integer iterator = 1;
		for (AelLaminaAps laminaAtual : laminasEmMemoria) {
			laminaAtual.setNumeroCapsula(iterator.toString());
			iterator++;
		}
	}
	
	public void excluir(){
		if(laminasEmMemoria.get(seqExcluir) != null && laminasEmMemoria.get(seqExcluir).getSeq() != null) {
				laminasParaRemover.add(laminasEmMemoria.get(seqExcluir));
		}
		laminasEmMemoria.remove(seqExcluir);
		ordenaLaminas();
		alteraValorCapsulas();
		//	this.limpar();
	}
	
	public String obterCampoTruncado(String valor, int tamanhoMaximo, boolean isTruncate){
		if(valor == null || valor.isEmpty()){
			return "";
		}else{
			return truncaCampo(valor, tamanhoMaximo, isTruncate);
		}
	}
	
	private String truncaCampo(String valor, int tamanhoMaximo, boolean isTruncate) {
		if(isTruncate && valor.length() > tamanhoMaximo){
			return valor.substring(0, tamanhoMaximo) + "...";
		}
		return valor;
	}

	public void desfazer(){
		this.limpar();
		this.inicio(luxSeq);
		alteraValorCapsulas();
	}
	
	public void gravar(){
		try {
			AelExameAp exameApOld = this.examesPatologiaFacade.obterAelExameApPorSeq(exameAp.getSeq()); 
			this.examesPatologiaFacade.persistirAelExameAp(exameAp, exameApOld);
			this.examesPatologiaFacade.gravarLaminas(laminasEmMemoria, laminasParaRemover, exameAp);
			
			laminasParaRemover = new ArrayList<AelLaminaAps>();
			
			this.apresentarMsgNegocio(Severity.INFO, "MSG_INDICE_BLOCOS_SALVO");
			this.laudoUnicoController.setDadosAlterados(false);
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		this.limpar();
	}
	
	public String getDescCestoTruncada(AelCestoPatologia cestoPat){
		return this.obterDescCestoTruncada(cestoPat);
	}
	
	public String obterDescCestoTruncada(AelCestoPatologia cestoPat) {
		if(cestoPat != null) {
			return cestoPat.getSigla() + " - " + StringUtil.trunc(cestoPat.getDescricao(), true, 15l) ;
		}
		return null;
	}
	
	public String obterDescColoracaoAbreviada(AelTextoPadraoColoracs coloracao){
		if(coloracao != null) {
			return coloracao.getDescricaoAbreviada();
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<AelCestoPatologia> pesquisarAelCestoPatologia(final String filtro){
		return this.returnSGWithCount(examesPatologiaFacade.pesquisarAelCestoPatologia((String)filtro, DominioSituacao.A),pesquisarAelCestoPatologiaCount(filtro)); 
	}
	
	public Long pesquisarAelCestoPatologiaCount(final String filtro){
		return examesPatologiaFacade.pesquisarAelCestoPatologiaCount((String)filtro, DominioSituacao.A);
	}

	public List<AelMaterialAp> pesquisaMateriaisCapsula(final String value){
		return returnSGWithCount(this.examesPatologiaFacade.pesquisaMateriaisCapsula(value, luxSeq), this.pesquisaMateriaisCapsulaCount(value));
	}

	public Long pesquisaMateriaisCapsulaCount(final String value){
		return this.examesPatologiaFacade.pesquisaMateriaisCapsulaCount(value, luxSeq);
	}
	
	public List<AelTextoPadraoColoracs> pesquisarAelTextoPadraoColoracs(final String filtro){
		return returnSGWithCount(examesPatologiaFacade.pesquisarAelTextoPadraoColoracs(filtro, DominioSituacao.A), this.pesquisarAelTextoPadraoColoracsCount(filtro));
	}
	
	public Long pesquisarAelTextoPadraoColoracsCount(final String filtro){
		return examesPatologiaFacade.pesquisarAelTextoPadraoColoracsCount(filtro, DominioSituacao.A);
	}
	
	public List<AelTextoPadraoColoracs> listarTextoPadraoColoracs() {
		return examesPatologiaFacade.listarAelTextoPadraoColoracs(DominioSituacao.A);
	}
	
	public List<AelCestoPatologia> getListaCestos() {
		return examesPatologiaFacade.pesquisarAelCestoPatologia(null, DominioSituacao.A);
	}
	
	public Date getDtLamina() {
		return dtLamina;
	}

	public void setDtLamina(Date dtLamina) {
		this.dtLamina = dtLamina;
	}

	public AelCestoPatologia getCesto() {
		return cesto;
	}

	public void setCesto(AelCestoPatologia cesto) {
		this.cesto = cesto;
	}

	public Integer getNroCapsulas() {
		return nroCapsulas;
	}

	public void setNroCapsulas(Integer nroCapsulas) {
		this.nroCapsulas = nroCapsulas;
	}

	public String getNroFragmentos() {
		return nroFragmentos;
	}

	public void setNroFragmentos(String nroFragmentos) {
		this.nroFragmentos = nroFragmentos;
	}

	public String getDsLamina() {
		return dsLamina;
	}

	public void setDsLamina(String dsLamina) {
		this.dsLamina = dsLamina;
	}

	public AelTextoPadraoColoracs getTextoPadraoColoracs() {
		return textoPadraoColoracs;
	}

	public void setTextoPadraoColoracs(AelTextoPadraoColoracs textoPadraoColoracs) {
		this.textoPadraoColoracs = textoPadraoColoracs;
	}

	public Long getLuxSeq() {
		return luxSeq;
	}

	public void setLuxSeq(Long luxSeq) {
		this.luxSeq = luxSeq;
	}

	public Boolean getEditando() {
		return editando;
	}

	public void setEditando(Boolean editando) {
		this.editando = editando;
	}

	public int getSeqExcluir() {
		return seqExcluir;
	}

	public void setSeqExcluir(int seqExcluir) {
		this.seqExcluir = seqExcluir;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public AelLaminaAps getLaminaEmMemoria() {
		return laminaEmMemoria;
	}

	public void setLaminaEmMemoria(AelLaminaAps laminaEmMemoria) {
		this.laminaEmMemoria = laminaEmMemoria;
	}

	public AelLaminaAps getLaminaEmEdicao() {
		return laminaEmEdicao;
	}

	public void setLaminaEmEdicao(AelLaminaAps laminaEmEdicao) {
		this.laminaEmEdicao = laminaEmEdicao;
	}

	public List<AelLaminaAps> getLaminasEmMemoria() {
		return laminasEmMemoria;
	}

	public void setLaminasEmMemoria(List<AelLaminaAps> laminasEmMemoria) {
		this.laminasEmMemoria = laminasEmMemoria;
	}

	public List<AelLaminaAps> getLaminasParaRemover() {
		return laminasParaRemover;
	}

	public void setLaminasParaRemover(List<AelLaminaAps> laminasParaRemover) {
		this.laminasParaRemover = laminasParaRemover;
	}

	public List<AelLaminaAps> getListaMateriaisGeracaoCapsulas() {
		return listaMateriaisGeracaoCapsulas;
	}

	public void setListaMateriaisGeracaoCapsulas(
			List<AelLaminaAps> listaMateriaisGeracaoCapsulas) {
		this.listaMateriaisGeracaoCapsulas = listaMateriaisGeracaoCapsulas;
	}

	public AelMaterialAp getMaterial() {
		return material;
	}

	public void setMaterial(AelMaterialAp material) {
		this.material = material;
	}

	public AelExameAp getExameAp() {
		return exameAp;
	}

	public void setExameAp(AelExameAp exameAp) {
		this.exameAp = exameAp;
	}
	
	public Boolean getGeracaoCapOk() {
		return geracaoCapOk;
	}

	public void setGeracaoCapOk(Boolean geracaoCapOk) {
		this.geracaoCapOk = geracaoCapOk;
	}

	public boolean isLaudoAntigo() {
		return laudoAntigo;
	}

	public void setLaudoAntigo(boolean laudoAntigo) {
		this.laudoAntigo = laudoAntigo;
	}

	public List<AelLaminaAps> getLaminas() {
		return laminas;
	}

	public void setLaminas(List<AelLaminaAps> laminas) {
		this.laminas = laminas;
	}

	public DynamicDataModel<AelLaminaAps> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AelLaminaAps> dataModel) {
		this.dataModel = dataModel;
	}

	public AelLaminaAps getLaminasEmMemoriaSelecionado() {
		return laminasEmMemoriaSelecionado;
	}

	public void setLaminasEmMemoriaSelecionado(
			AelLaminaAps laminasEmMemoriaSelecionado) {
		this.laminasEmMemoriaSelecionado = laminasEmMemoriaSelecionado;
	}	
}