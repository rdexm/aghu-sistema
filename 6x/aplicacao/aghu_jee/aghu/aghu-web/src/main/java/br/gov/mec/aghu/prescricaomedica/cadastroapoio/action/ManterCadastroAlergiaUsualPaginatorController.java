package br.gov.mec.aghu.prescricaomedica.cadastroapoio.action;

import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.exception.ConstraintViolationException;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAlergiaUsual;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

public class ManterCadastroAlergiaUsualPaginatorController extends ActionController {

	private static final long serialVersionUID = -904568306603740968L;

	private static final String PAGE_MANTER_CADASTRO_ALERGIA_USUAL_CRUD = "prescricaomedica-manterCadastroAlergiaUsualCRUD";
	
	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ManterCadastroAlergiaUsualController manterCadastroAlergiaUsualController;

	private MpmAlergiaUsual parametroSelecionado;
	
	private List<MpmAlergiaUsual> listaAlergiaUsual;
	
	private Integer codigo;
	
	private String descricao;

	private DominioSimNao indSituacao;
	
	private DominioSituacao situacao;
	
	private boolean pesquisaAtiva = false;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	public void pesquisar() {
		if(this.indSituacao != null && this.indSituacao.isSim()){
			setSituacao(DominioSituacao.A);
		}else if(this.indSituacao != null && !this.indSituacao.isSim()){
			setSituacao(DominioSituacao.I);
		}else{
			setSituacao(null);
		}
		setListaAlergiaUsual(cadastrosBasicosPrescricaoMedicaFacade.pesquisarAlergiaUsual(this.codigo, this.descricao, this.situacao));
		setPesquisaAtiva(true);
	}
	
	public void limparPesquisa() {
		
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance()
				.getViewRoot().getFacetsAndChildren();

		while (componentes.hasNext()) {
			limparValoresSubmetidos(componentes.next());
		}
		this.codigo = null;
		this.descricao = null;
		setPesquisaAtiva(false);
		this.indSituacao = null;
		this.listaAlergiaUsual = null;
		
	}
	
	private void limparValoresSubmetidos(Object object) {

		if (object == null || object instanceof UIComponent == false) {
			return;
		}

		Iterator<UIComponent> uiComponent = ((UIComponent) object)
				.getFacetsAndChildren();

		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}

		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}

	public void excluir() throws ApplicationBusinessException {
		try{
			this.cadastrosBasicosPrescricaoMedicaFacade.removerAlergiaUsual(parametroSelecionado);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_ALERGIA_USUAL");
		} catch (BaseException e) {
			if (e.getCause() instanceof ConstraintViolationException) {
				apresentarMsgNegocio(Severity.ERROR, "VIOLACAO_FK_ALERGIA_USUAL");
			}
		} catch (Exception e){
			if (e.getCause() instanceof ConstraintViolationException
					|| e.getCause().getCause() instanceof ConstraintViolationException
					|| e.getCause().getCause().getCause() instanceof ConstraintViolationException) {
				apresentarMsgNegocio(Severity.ERROR, "VIOLACAO_FK_ALERGIA_USUAL");
			}
		}
		pesquisar();
	}

	public String editar() {
		manterCadastroAlergiaUsualController.setMpmAlergiaUsual(parametroSelecionado);
		if(parametroSelecionado.getIndSituacao().equals(DominioSituacao.A.toString())){
			manterCadastroAlergiaUsualController.setIndSituacao(true);
		}else {
			manterCadastroAlergiaUsualController.setIndSituacao(false);
		}
		parametroSelecionado = new MpmAlergiaUsual();
		return PAGE_MANTER_CADASTRO_ALERGIA_USUAL_CRUD;
	}
	
	public String inserir() {
		return PAGE_MANTER_CADASTRO_ALERGIA_USUAL_CRUD;
	}
	
	public String obterUltimoResponsavel(Integer serMatricula, Short vinCodigo){
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorPorChavePrimaria(serMatricula, vinCodigo);
		String ultimoResponsavel = servidorLogado.getUsuario();
		return ultimoResponsavel;
	}
	
	public String obterHint(String item, Integer tamanhoMaximo) {
		if (item.length() > tamanhoMaximo) {
			item = StringUtils.abbreviate(item, tamanhoMaximo);
		}
		return item;
	}

	/*
	 * Getters and Setters
	 */

	public MpmAlergiaUsual getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(MpmAlergiaUsual parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSimNao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSimNao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public List<MpmAlergiaUsual> getListaAlergiaUsual() {
		return listaAlergiaUsual;
	}

	public void setListaAlergiaUsual(List<MpmAlergiaUsual> listaAlergiaUsual) {
		this.listaAlergiaUsual = listaAlergiaUsual;
	}

	public boolean isPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

}
