package br.gov.mec.aghu.sig.custos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.SigGrupoOcupacaoCargos;
import br.gov.mec.aghu.model.SigGrupoOcupacoes;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller responsavel pela estória #30139 - Inclusão de CARGOS EM LOTE NO GRUPO DE OCUPAÇÃO
 * 
 * 
 * @author jgugel
 *
 */
public class AdicionarGrupoOcupacaoEmLoteController extends ActionController {

	private static final String GRUPO_OCUPACAO_CRUD = "grupoOcupacaoCRUD";

	private static final long serialVersionUID = 6649612877148582990L;

	@Inject
	private GrupoOcupacaoController grupoOcupacaoController;

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	
	private SigGrupoOcupacoes grupoOcupacao;
	private String descricao;
	private String carCodigo;
	private Integer codigo;
	
	private List<SigGrupoOcupacaoCargos> listGrupoOcupacaoCargos;
	
	private boolean marcarLote;
	private boolean marcouTodos;

	private boolean ativo = false;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	/**
	 * Metodo iniciar, seta propriedades iniciais
	 */
	public void iniciar() {
		this.setDescricao("");
		this.setCarCodigo("");
		this.setCodigo(null);
		this.marcarLote = false;
		this.marcouTodos = false;
		this.ativo = false;
		setListGrupoOcupacaoCargos(new ArrayList<SigGrupoOcupacaoCargos>());
	}

	
	/**
	 * Efetua a pesquisa dos cargos, apenas não efetua a pesquisa caso codigo e nem descrição tenham informações
	 * @throws ApplicationBusinessException
	 */
	public void pesquisar() throws ApplicationBusinessException {
		
		if ((this.descricao == null || this.descricao.equals("")) && (this.codigo == null)) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_PESQUISA_OBRIGATORIO_NOME_OU_CODIGO_CARGO_LOTE");
			return;
		}

		this.marcarLote = false;
		this.marcouTodos = false;
		this.ativo = true;
		listGrupoOcupacaoCargos = new ArrayList<SigGrupoOcupacaoCargos>();

		List<RapOcupacaoCargo> listOcupacaoCargo = cadastrosBasicosFacade.pesquisarOcupacaoCargo(codigo, descricao, carCodigo, DominioSituacao.A);
		for (RapOcupacaoCargo rapOcupacaoCargo : listOcupacaoCargo) {
			SigGrupoOcupacaoCargos ocupacao = new SigGrupoOcupacaoCargos();
			ocupacao.setIndSituacao(DominioSituacao.A);
			ocupacao.setRapOcupacaoCargo(rapOcupacaoCargo);
			this.getListGrupoOcupacaoCargos().add(ocupacao);
		}
	}

	/**
	 * Retorna a tela anterior
	 * @return
	 */
	public String cancelar() {
		return GRUPO_OCUPACAO_CRUD;
	}
	
	
	/**
	 * Adiciona os cargos selecionados e retorna para a tela anterior
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String adicionar() throws ApplicationBusinessException {
		List<SigGrupoOcupacaoCargos> listGrupoOcupacaoCargosLote = new ArrayList<SigGrupoOcupacaoCargos>();
		
		for (SigGrupoOcupacaoCargos cargoIt : listGrupoOcupacaoCargos) {
			if(cargoIt.getSelected()) {
				listGrupoOcupacaoCargosLote.add(cargoIt);
			}
		}
		
		if (listGrupoOcupacaoCargosLote.isEmpty() || listGrupoOcupacaoCargosLote.size() == 0) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SEM_ITEMS_SELECIONADO_CARGO_LOTE");
			return null;
		}
		
		this.grupoOcupacaoController.adicionaCargoEmLote(listGrupoOcupacaoCargosLote);
		
		return GRUPO_OCUPACAO_CRUD;
	}

	/**
	 * Controlle interno da selectBox
	 * @param grupo
	 */
	public void selectedAdicionarGrupo(SigGrupoOcupacaoCargos grupo) {
		for (SigGrupoOcupacaoCargos grupoIt :  listGrupoOcupacaoCargos) {
			if (grupo.getRapOcupacaoCargo().getId().getCargoCodigo().equalsIgnoreCase(grupoIt.getRapOcupacaoCargo().getId().getCargoCodigo())
					&& grupo.getRapOcupacaoCargo().getId().getCodigo() == grupoIt.getRapOcupacaoCargo().getId().getCodigo()){
				grupoIt.setSelected(grupo.getSelected());
				return;
			}
		}
	}

	public void selecionaLote() {
		marcarLote = !this.marcarLote;//Necessário já que o p:ajax não estava enviando o valor atual do checkbox
		marcouTodos = this.marcarLote;
		for (SigGrupoOcupacaoCargos grupoIt :  listGrupoOcupacaoCargos) {
			grupoIt.setSelected(marcouTodos);
		}
	}

		
	// Getters and Setters	
	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public SigGrupoOcupacoes getGrupoOcupacao() {
		return grupoOcupacao;
	}

	public void setGrupoOcupacao(SigGrupoOcupacoes grupoOcupacao) {
		this.grupoOcupacao = grupoOcupacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getCarCodigo() {
		return carCodigo;
	}

	public void setCarCodigo(String carCodigo) {
		this.carCodigo = carCodigo;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public List<SigGrupoOcupacaoCargos> getListGrupoOcupacaoCargos() {
		return listGrupoOcupacaoCargos;
	}

	public void setListGrupoOcupacaoCargos(List<SigGrupoOcupacaoCargos> listGrupoOcupacaoCargos) {
		this.listGrupoOcupacaoCargos = listGrupoOcupacaoCargos;
	}

	public boolean isMarcouTodos() {
		return marcouTodos;
	}

	public void setMarcouTodos(boolean marcouTodos) {
		this.marcouTodos = marcouTodos;
	}
}
