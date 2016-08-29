package br.gov.mec.aghu.prescricaomedica.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.model.AfaFormulaNptPadrao;
import br.gov.mec.aghu.model.AfaTipoComposicoes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.AfaComposicaoNptPadraoVO;
import br.gov.mec.aghu.prescricaomedica.vo.AfaItemNptPadraoVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmComposicaoPrescricaoNptVO;
import br.gov.mec.aghu.prescricaomedica.vo.MpmItemPrescricaoNptVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class EscolherFormulaNPTController extends ActionController{	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4172720203767602572L;

	private String descricao = StringUtils.EMPTY;

	private AfaFormulaNptPadrao afaFormulaNptPadrao = new AfaFormulaNptPadrao();

	private AfaComposicaoNptPadraoVO afaComposicaoNptPadraoVO = new AfaComposicaoNptPadraoVO();

	private AfaItemNptPadraoVO item3 = new AfaItemNptPadraoVO();

	private boolean pesquisaAtiva = false;

	private boolean pesquisaAtiva2 = false;

	private boolean pesquisaAtiva3 = false;

	private List<AfaFormulaNptPadrao> lista = new ArrayList<AfaFormulaNptPadrao>();

	private List<AfaComposicaoNptPadraoVO> lista2 = new ArrayList<AfaComposicaoNptPadraoVO>();

	private List<AfaItemNptPadraoVO> lista3 = new ArrayList<AfaItemNptPadraoVO>();

	private List<AfaItemNptPadraoVO> lista4 = new ArrayList<AfaItemNptPadraoVO>();

	private RapServidores servidorLogado;

	private AfaTipoComposicoes afaTipoComposicoes;

	private Boolean permissao;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@Inject
	CadastroPrescricaoNptController cadastroPrescricaoNptController;

	private static final String VOLTAR = "cadastroPrescricaoNpt";

	private static final String vazio= "";              


	@PostConstruct
	public void inicializar() {
		begin(conversation, true);
	}

	public void inicio() throws ApplicationBusinessException {
		pesquisar();
	}

	public void selecionarItem(){
		lista2 = new ArrayList<AfaComposicaoNptPadraoVO>();
	}

	public void limparPesquisa() {
		afaFormulaNptPadrao = new AfaFormulaNptPadrao();
		afaComposicaoNptPadraoVO = new AfaComposicaoNptPadraoVO();
		item3 = new AfaItemNptPadraoVO();
		descricao = vazio;
		lista.clear();
		lista2.clear();
		lista3.clear();
		pesquisaAtiva = false;
		pesquisaAtiva2 = false;
		pesquisaAtiva3 = false;
		pesquisar();
	}

	public String selecionar(){
		if(afaFormulaNptPadrao.getSeq() == null){

			apresentarMsgNegocio(Severity.ERROR, "MSG1_SELECIONAR_FORMULA");
			return null;
		}
		cadastroPrescricaoNptController.setFnpSeq(afaFormulaNptPadrao.getSeq());
		cadastroPrescricaoNptController.setDescricaoFormula(afaFormulaNptPadrao.getDescricao());
		
		//#48702
		MpmComposicaoPrescricaoNptVO primeiroItemSelecionado = null;
		if (lista2 != null) {
			List<MpmComposicaoPrescricaoNptVO> listaComposicoes = new ArrayList<MpmComposicaoPrescricaoNptVO>();
			for (AfaComposicaoNptPadraoVO afaComposicaoNptPadraoVO : lista2) {
				MpmComposicaoPrescricaoNptVO composicaoVO = new MpmComposicaoPrescricaoNptVO();
				composicaoVO.setComposicaoDescricao(afaComposicaoNptPadraoVO.getDescricao());
				composicaoVO.setTicSeq(afaComposicaoNptPadraoVO.getSeqComposicao());
				composicaoVO.setVelocidadeAdministracao(afaComposicaoNptPadraoVO.getVelocidadeAdministracao() == null ? null : BigDecimal.valueOf(afaComposicaoNptPadraoVO.getVelocidadeAdministracao()));
				composicaoVO.setUnidade(afaComposicaoNptPadraoVO.getDescricaovelAdministracao());
				composicaoVO.setTvaSeq(afaComposicaoNptPadraoVO.getSeqVelAdministracao());
				
				if(primeiroItemSelecionado == null){
					primeiroItemSelecionado = composicaoVO;
				}
		
				cadastroPrescricaoNptController.setComposicaoSelecionada(composicaoVO);	
	
				this.afaComposicaoNptPadraoVO = afaComposicaoNptPadraoVO;
				pesquisarListaComponentes();
				if (lista3 != null) {
					List<MpmItemPrescricaoNptVO> listaItem = new ArrayList<MpmItemPrescricaoNptVO>();
					for (AfaItemNptPadraoVO afaItem : lista3) {
						MpmItemPrescricaoNptVO item = new MpmItemPrescricaoNptVO();
						item.setDescricaoComponente(afaItem.getDescricaoComponenteNpts());
						item.setFdsSeq(afaItem.getFdsSeq());
						item.setCnpMedMatCodigo(afaItem.getMedMatCodigoComponenteNpts());
						item.setUnidadeComponente(afaItem.getUnidadeMedica());
						item.setUmmSeq(afaItem.getUmmSeq() );
						item.setQtdePrescrita(afaItem.getQtdItemNpt() == null ? null : BigDecimal.valueOf(afaItem.getQtdItemNpt()));
						listaItem.add(item);
					}
					composicaoVO.setComponentes(listaItem);
					cadastroPrescricaoNptController.setComponentes(listaItem);
				}
				listaComposicoes.add(composicaoVO);
			}
			cadastroPrescricaoNptController.setComposicoes(listaComposicoes);
			if(primeiroItemSelecionado != null){
				cadastroPrescricaoNptController.setComposicaoSelecionada(primeiroItemSelecionado);	
			}
		}
		limparPesquisa();
		//#48702
		return VOLTAR;
	}

	public String voltar(){
		limparPesquisa();
		return VOLTAR;
	}

	public void pesquisar() {
		lista = prescricaoMedicaFacade.listarFormulaNptPadrao();
		definirSelecaoPadraoListas();
		if(!lista.isEmpty()){
			setPesquisaAtiva(true);
		}
	}
	
	private void definirSelecaoPadraoListas() {
		if (lista != null && !lista.isEmpty()) {
			afaFormulaNptPadrao = lista.get(0);
			pesquisarListaComposicao();
			if (lista2 != null && !lista2.isEmpty()) {
				afaComposicaoNptPadraoVO = lista2.get(0);
				pesquisarListaComponentes();
				if (lista3 != null && !lista3.isEmpty()) {
					item3 = lista3.get(0);
				}
			}
		}
	}

	/**
	 * Lista de grupos de composições relacionados com a linha selecionada na grid principal
	 */
	public void pesquisarListaComposicao(){
		afaTipoComposicoes = null;
		lista2.clear();
		lista3.clear();
		lista2 = prescricaoMedicaFacade.obterListaComposicaoNptPadraoVO(afaFormulaNptPadrao.getSeq());
		if (lista2 != null && !lista2.isEmpty()) {
			afaComposicaoNptPadraoVO = lista2.get(0);
			pesquisarListaComponentes();
		}
		if(!lista2.isEmpty()){
			setPesquisaAtiva2(true);
		}
	}

	public void pesquisarListaComponentes(){
		lista3.clear();
		lista3 = prescricaoMedicaFacade.obterListaAfaItemNptPadraoOrder(afaComposicaoNptPadraoVO.getIdComposicaoSeqP(), afaComposicaoNptPadraoVO.getIdComposicaoFnpSeq());
		if (lista3 != null && !lista3.isEmpty()) {
			item3 = lista3.get(0);
		}
		if(!lista3.isEmpty()){
			setPesquisaAtiva3(true);
		}

	}

	public String truncaDescricao(String desc){
		if(!desc.isEmpty() && (desc.length() > 41)){
			desc = desc.substring(0, 50) + " ...";
		}
		return desc;
	}

	public Boolean getPermissao() {
		return permissao;
	}

	public void setPermissao(Boolean permissao) {
		this.permissao = permissao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public AfaFormulaNptPadrao getAfaFormulaNptPadrao() {
		return afaFormulaNptPadrao;
	}

	public void setAfaFormulaNptPadrao(AfaFormulaNptPadrao afaFormulaNptPadrao) {
		this.afaFormulaNptPadrao = afaFormulaNptPadrao;
	}

	public List<AfaComposicaoNptPadraoVO> getLista2() {
		return lista2;
	}

	public void setLista2(List<AfaComposicaoNptPadraoVO> lista2) {
		this.lista2 = lista2;
	}

	public List<AfaFormulaNptPadrao> getLista() {
		return lista;
	}

	public void setLista(List<AfaFormulaNptPadrao> lista) {
		this.lista = lista;
	}

	public static String getVazio() {
		return vazio;
	}

	public boolean isPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	public boolean isPesquisaAtiva2() {
		return pesquisaAtiva2;
	}

	public void setPesquisaAtiva2(boolean pesquisaAtiva2) {
		this.pesquisaAtiva2 = pesquisaAtiva2;
	}

	public AfaComposicaoNptPadraoVO getAfaComposicaoNptPadraoVO() {
		return afaComposicaoNptPadraoVO;
	}

	public void setAfaComposicaoNptPadraoVO(AfaComposicaoNptPadraoVO afaComposicaoNptPadraoVO) {
		this.afaComposicaoNptPadraoVO = afaComposicaoNptPadraoVO;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}

	public boolean isPesquisaAtiva3() {
		return pesquisaAtiva3;
	}

	public void setPesquisaAtiva3(boolean pesquisaAtiva3) {
		this.pesquisaAtiva3 = pesquisaAtiva3;
	}

	public List<AfaItemNptPadraoVO> getlista3() {
		return lista3;
	}

	public void setlista3(List<AfaItemNptPadraoVO> lista3) {
		this.lista3 = lista3;
	}

	public AfaTipoComposicoes getAfaTipoComposicoes() {
		return afaTipoComposicoes;
	}

	public void setAfaTipoComposicoes(AfaTipoComposicoes afaTipoComposicoes) {
		this.afaTipoComposicoes = afaTipoComposicoes;
	}

	public List<AfaItemNptPadraoVO> getLista3() {
		return lista3;
	}

	public void setLista3(List<AfaItemNptPadraoVO> lista3) {
		this.lista3 = lista3;
	}

	public List<AfaItemNptPadraoVO> getLista4() {
		return lista4;
	}

	public void setLista4(List<AfaItemNptPadraoVO> lista4) {
		this.lista4 = lista4;
	}

	public AfaItemNptPadraoVO getItem3() {
		return item3;
	}

	public void setItem3(AfaItemNptPadraoVO item3) {
		this.item3 = item3;
	}


}
