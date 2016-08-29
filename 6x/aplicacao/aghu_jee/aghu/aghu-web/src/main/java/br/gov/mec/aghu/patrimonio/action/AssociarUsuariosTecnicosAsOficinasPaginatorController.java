package br.gov.mec.aghu.patrimonio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.vo.UsuarioTecnicoVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class AssociarUsuariosTecnicosAsOficinasPaginatorController extends ActionController{

	/**
	 * 
	 */
	private static final long serialVersionUID = 486524L;
	
	private final String PAGE_CADASTRAR_OFICINAS = "areaTecnicaAvaliacaoList";
	private final String PAGE_ASSOCIAR_USUARIOS_TECNICOS = "/pages/patrimonio/avaliacaotecnica/associarUsuariosTecnicosAsOficinas.xhtml";
	
	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	@EJB 
	private IServidorLogadoFacade servidorLogadoFacade;
	
	
	private RapServidores tecnicoSB1;
	private UsuarioTecnicoVO tecnicoOnMouseOver = new UsuarioTecnicoVO();
	
	private UsuarioTecnicoVO acaoSelection;
	
	private UsuarioTecnicoVO acaoTecnicoPadrao;
	
	private PtmAreaTecAvaliacao areaTecnicaSelecionada;
	
	private List <UsuarioTecnicoVO> tecnicosList = new ArrayList<UsuarioTecnicoVO>();
	private List <UsuarioTecnicoVO> tecnicosExcluidos = new ArrayList<UsuarioTecnicoVO>();
	private List <UsuarioTecnicoVO> tecnicosAdicionados = new ArrayList<UsuarioTecnicoVO>();
	
	private RapServidores servidorLogado;
	
	@PostConstruct
	public void inicializar() {
		begin(conversation);
		tecnicoSB1 = null;
		tecnicosList = patrimonioFacade.obterUsuariosTecnicosList(areaTecnicaSelecionada);
		servidorLogado = servidorLogadoFacade.obterServidorLogado();
	}
	
	

	//Adiciona o servidor da SB para a lista que esta sendo mostrada
	//RN06
	public void adicionar(){
		if(this.patrimonioFacade.verificarChefeParaAreaTecnica(areaTecnicaSelecionada.getSeq(), servidorLogado)){			
			if(tecnicoSB1 != null){
				try {
					if(tecnicoSB1.getUsuario() != null){
						patrimonioFacade.validarAssociacaoDeTecnicoAreaAvaliacao(tecnicosList, tecnicoSB1, areaTecnicaSelecionada.getSeq());
						UsuarioTecnicoVO novo = new UsuarioTecnicoVO();
						UsuarioTecnicoVO novo1 = new UsuarioTecnicoVO();
						novo.setNome(tecnicoSB1.getPessoaFisica().getNome());
						novo.setMatRapTecnico(tecnicoSB1.getId().getMatricula());
						novo.setSerVinCodigoTecnico(tecnicoSB1.getId().getVinCodigo());
						novo.setSeqAreaTecAvaliacao(areaTecnicaSelecionada.getSeq());
						novo.setMatRapCriacao(servidorLogado.getId().getMatricula());
						novo.setSerVinCodigoCriacao(servidorLogado.getId().getVinCodigo());
						novo.setTecnicoPadrao(Boolean.FALSE);
						novo1 = novo;
						tecnicosAdicionados.add(novo);
						tecnicosList.add(novo1);
						tecnicoSB1 = null;
					}else{
						apresentarMsgNegocio(Severity.WARN, "USUARIO_NAO_ENCONTRADO_TEC_SELECIONADO");
					}
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
			}
		}else{
			apresentarMsgNegocio(Severity.INFO, "CHEFE_SEM_PERMISSAO");
		}
	}
	
	public void adcionarTecnicoPadrao(){
		if(this.patrimonioFacade.verificarChefeParaAreaTecnica(areaTecnicaSelecionada.getSeq(), servidorLogado)){
			if(acaoTecnicoPadrao != null && acaoTecnicoPadrao.getTecnicoPadrao() != null){
				if(!acaoTecnicoPadrao.getTecnicoPadrao()){
					try {
						patrimonioFacade.validarUnicidadeTecnicoPadrao(tecnicosList);
						editarTecnico(true);
					} catch (ApplicationBusinessException e) {
						apresentarExcecaoNegocio(e);
					}
				}else{
					editarTecnico(false);
				}
			}
			acaoTecnicoPadrao = null;
		}else{
			apresentarMsgNegocio(Severity.INFO, "CHEFE_SEM_PERMISSAO");
		}
	}
	
	public void editarTecnico(Boolean valor){
		//Altera valor de tecnico padrao do item que ja existe na lista da dataTable
		for (UsuarioTecnicoVO voItem : tecnicosList) {
			if(voItem.getMatRapTecnico().equals(acaoTecnicoPadrao.getMatRapTecnico()) && voItem.getSerVinCodigoTecnico().equals(acaoTecnicoPadrao.getSerVinCodigoTecnico())){
				voItem.setTecnicoPadrao(valor);
			}
		}
		
		boolean achou = false;
		
		//Adiciona ou edita, se ja existente, na lista de tecnicosAdicionados para persistencia no banco
		for (UsuarioTecnicoVO vo : tecnicosAdicionados) {
			if(acaoTecnicoPadrao.getMatRapTecnico().equals(vo.getMatRapTecnico()) && acaoTecnicoPadrao.getSerVinCodigoTecnico().equals(vo.getSerVinCodigoTecnico())){
				vo.setTecnicoPadrao(valor);
				achou = true;
			}
		}
		if(!achou){
			tecnicosAdicionados.add(acaoTecnicoPadrao);
		}
	}
	
	//Exclui um servidor da lista que esta sendo mostrada
	public void excluir(){
		if(this.patrimonioFacade.verificarChefeParaAreaTecnica(areaTecnicaSelecionada.getSeq(), servidorLogado)){
			for(UsuarioTecnicoVO vo : tecnicosAdicionados){
				if(acaoSelection.getMatRapTecnico().equals(vo.getMatRapTecnico()) && acaoSelection.getSerVinCodigoTecnico().equals(vo.getSerVinCodigoTecnico())){
					tecnicosAdicionados.remove(vo);
					break;
				}
			}
			for(UsuarioTecnicoVO vo : tecnicosList){
				if(acaoSelection.getMatRapTecnico().equals(vo.getMatRapTecnico()) && acaoSelection.getSerVinCodigoTecnico().equals(vo.getSerVinCodigoTecnico())){
					tecnicosExcluidos.add(vo);
					tecnicosList.remove(vo);
					acaoSelection = null;
					apresentarMsgNegocio(Severity.INFO, "USUARIO_TECNICO_REMOVIDO_SUCESSO");
					break;
				}
			}
		}else{
			apresentarMsgNegocio(Severity.INFO, "CHEFE_SEM_PERMISSAO");
		}
	}
	
	public void gravar(){
		patrimonioFacade.gravarAlteracoesAssociarTecnicoPadrao(tecnicosExcluidos, tecnicosAdicionados);
		tecnicosExcluidos = new ArrayList<UsuarioTecnicoVO>();
		tecnicosAdicionados = new ArrayList<UsuarioTecnicoVO>();
		tecnicosList = patrimonioFacade.obterUsuariosTecnicosList(areaTecnicaSelecionada);
		tecnicoSB1 = null;
		apresentarMsgNegocio(Severity.INFO, "USUARIO_TECNICO_ASSOCIADO_SUCESSO");
	}
	
	public String associarUsuariosTecnicos(){
		tecnicosExcluidos = new ArrayList<UsuarioTecnicoVO>();
		tecnicosAdicionados = new ArrayList<UsuarioTecnicoVO>();
		tecnicosList = patrimonioFacade.obterUsuariosTecnicosList(areaTecnicaSelecionada);
		tecnicoSB1 = null;
		return PAGE_ASSOCIAR_USUARIOS_TECNICOS;
	}
	
	public String voltar(){
		return PAGE_CADASTRAR_OFICINAS;
	}
	
	/**
	 * Truncar os itens e adiciona o símbolo de reticências (...)
	 * 
	 * @param item
	 * @return
	 */
	public String truncarNome(String item, Integer tamanhoMaximo) {
		String itemCapitalizado = WordUtils.capitalizeFully(item);
		if (itemCapitalizado.length() > tamanhoMaximo) {
			itemCapitalizado = StringUtils.abbreviate(itemCapitalizado, tamanhoMaximo);
		}
			
		return itemCapitalizado;
	}
	
	public String descricaoSuggestion(){
		StringBuffer descricao = new StringBuffer();
		String s = (tecnicoSB1.getId().getVinCodigo()+" - "+tecnicoSB1.getServidor().getPessoaFisica().getNome());
		descricao.append(s);
		return descricao.toString();
	}
	
	//C2 SB1
	public List<RapServidores> obterUsuariosTecnicos(String paramentro){
		return returnSGWithCount(patrimonioFacade.obterusuariosTecnicosPorVinculoMatNome(paramentro), patrimonioFacade.obterusuariosTecnicosPorVinculoMatNomeCount(paramentro));
	}

	public IPatrimonioFacade getPatrimonioFacade() {
		return patrimonioFacade;
	}

	public void setPatrimonioFacade(IPatrimonioFacade patrimonioFacade) {
		this.patrimonioFacade = patrimonioFacade;
	}

	public UsuarioTecnicoVO getTecnicoOnMouseOver() {
		return tecnicoOnMouseOver;
	}

	public void setTecnicoOnMouseOver(UsuarioTecnicoVO tecnicoOnMouseOver) {
		this.tecnicoOnMouseOver = tecnicoOnMouseOver;
	}

	public UsuarioTecnicoVO getAcaoSelection() {
		return acaoSelection;
	}

	public void setAcaoSelection(UsuarioTecnicoVO acaoSelection) {
		this.acaoSelection = acaoSelection;
	}

	public PtmAreaTecAvaliacao getAreaTecnicaSelecionada() {
		return areaTecnicaSelecionada;
	}

	public void setAreaTecnicaSelecionada(PtmAreaTecAvaliacao areaTecnicaSelecionada) {
		this.areaTecnicaSelecionada = areaTecnicaSelecionada;
	}

	public List<UsuarioTecnicoVO> getTecnicosList() {
		return tecnicosList;
	}

	public void setTecnicosList(List<UsuarioTecnicoVO> tecnicosList) {
		this.tecnicosList = tecnicosList;
	}

	public List<UsuarioTecnicoVO> getTecnicosExcluidos() {
		return tecnicosExcluidos;
	}

	public void setTecnicosExcluidos(List<UsuarioTecnicoVO> tecnicosExcluidos) {
		this.tecnicosExcluidos = tecnicosExcluidos;
	}

	public List<UsuarioTecnicoVO> getTecnicosAdicionados() {
		return tecnicosAdicionados;
	}

	public void setTecnicosAdicionados(List<UsuarioTecnicoVO> tecnicosAdicionados) {
		this.tecnicosAdicionados = tecnicosAdicionados;
	}

	public RapServidores getTecnicoSB1() {
		return tecnicoSB1;
	}

	public void setTecnicoSB1(RapServidores tecnicoSB1) {
		this.tecnicoSB1 = tecnicoSB1;
	}

	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public void setServidorLogadoFacade(IServidorLogadoFacade servidorLogadoFacade) {
		this.servidorLogadoFacade = servidorLogadoFacade;
	}

	public RapServidores getServidorLogado() {
		return servidorLogado;
	}

	public void setServidorLogado(RapServidores servidorLogado) {
		this.servidorLogado = servidorLogado;
	}

	public UsuarioTecnicoVO getAcaoTecnicoPadrao() {
		return acaoTecnicoPadrao;
	}

	public void setAcaoTecnicoPadrao(UsuarioTecnicoVO acaoTecnicoPadrao) {
		this.acaoTecnicoPadrao = acaoTecnicoPadrao;
	}

}
