package br.gov.mec.aghu.sig.custos.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapOcupacaoCargo;
import br.gov.mec.aghu.model.SigAtividadePessoas;
import br.gov.mec.aghu.model.SigGrupoOcupacaoCargos;
import br.gov.mec.aghu.model.SigGrupoOcupacoes;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class GrupoOcupacaoController extends ActionController {

	private static final String ADICIONAR_GRUPO_OCUPACAO_LOTE = "adicionarGrupoOcupacaoLote";

	private static final String GRUPO_OCUPACAO_LIST = "grupoOcupacaoList";

	private static final long serialVersionUID = -1523156489794731570L;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;
	
	@Inject
	private AdicionarGrupoOcupacaoEmLoteController adicionarGrupoOcupacaoEmLoteController;

	private Integer seqGrupoOcupacao;
	private SigGrupoOcupacoes grupoOcupacao;
	private String descricao;
	private FccCentroCustos centroCusto;
	private DominioSituacao situacao;
	private List<SigGrupoOcupacaoCargos> listaCargos;

	private Integer indiceGrupoOcupacaoCargo;
	private SigGrupoOcupacaoCargos grupoOcupacaoCargos;
	private RapOcupacaoCargo ocupacaoCargo;
	private DominioSituacao situacaoOcupacaoCargo;

	private Boolean edicao;
	private Boolean exibirMensagemConfirmacaoInativacao;
	private Boolean possuiAlteracoesPendentes;

	private boolean adicionouCargosEmLote;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
		
		if(!adicionouCargosEmLote){
			this.limpar();
			this.setPossuiAlteracoesPendentes(false);
			if (this.getSeqGrupoOcupacao() == null) {
				this.setEdicao(false);
				this.setGrupoOcupacao(new SigGrupoOcupacoes());
				this.setListaCargos(this.getGrupoOcupacao().getListGrupoOcupacaoCargos());
				this.setSituacao(DominioSituacao.A);
			} else {
				this.setEdicao(true);
				this.setGrupoOcupacao(this.custosSigCadastrosBasicosFacade.obterGrupoOcupacao(this.getSeqGrupoOcupacao()));
	
				if (this.getGrupoOcupacao() != null) {
					this.setDescricao(this.getGrupoOcupacao().getDescricao());
					this.setSituacao(this.getGrupoOcupacao().getIndSituacao());
					this.setCentroCusto(this.getGrupoOcupacao().getFccCentroCustos());
					this.setListaCargos(this.getGrupoOcupacao().getListGrupoOcupacaoCargos());
					this.reordenarListaGrupoOcupacaoCargo();
				} else {
					this.setSeqGrupoOcupacao(null);
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_GRUPO_OCUPACAO_INEXISTENTE");
					this.iniciar();
				}
			}
		}
		adicionouCargosEmLote = false;
	}
	

	protected void limpar() {

		this.setGrupoOcupacao(null);
		this.setDescricao(null);
		this.setSituacao(null);
		this.setCentroCusto(null);
		this.setListaCargos(null);

		this.setGrupoOcupacaoCargos(null);
		this.limparCamposOcupacaoCargo();

		this.setEdicao(false);
		this.setExibirMensagemConfirmacaoInativacao(false);
		
	}

	public void editar(Integer indice) {
		
		this.setIndiceGrupoOcupacaoCargo(indice);
		
		if (this.getIndiceGrupoOcupacaoCargo() != null) {
			// Determina o elemento dentro da lista que será editado
			this.setGrupoOcupacaoCargos(this.getListaCargos().get(this.getIndiceGrupoOcupacaoCargo()));
			// Armazena os dados nas variaveis correspondentes aos campos
			this.setSituacaoOcupacaoCargo(this.getGrupoOcupacaoCargos().getIndSituacao());
			this.setOcupacaoCargo(this.getGrupoOcupacaoCargos().getRapOcupacaoCargo());
			this.setPossuiAlteracoesPendentes(true);
		}
	}

	public void excluir() {
		int posicaoNaLista = this.getIndiceGrupoOcupacaoCargo();
		
		if(posicaoNaLista >= 0){
			this.getListaCargos().remove(posicaoNaLista);
			this.limparCamposOcupacaoCargo();
			this.setPossuiAlteracoesPendentes(true);
		}
	}

	public void adicionar() {

		try {
			// Cria o grupo de ocupação
			SigGrupoOcupacaoCargos grupoOcupacaoCargo = new SigGrupoOcupacaoCargos();
			grupoOcupacaoCargo.setIndSituacao(this.getSituacaoOcupacaoCargo());
			grupoOcupacaoCargo.setRapOcupacaoCargo(this.getOcupacaoCargo());

			// Valida se o cargo já está no grupo de ocupação
			this.custosSigCadastrosBasicosFacade.validarRepeticaoCargosGrupoOcupacao(this.getGrupoOcupacao(), this.getListaCargos(), this.getOcupacaoCargo(), this
					.getListaCargos().size());

			// Adiciona na lista
			this.getListaCargos().add(grupoOcupacaoCargo);

			// E reordenar pela descricao
			this.reordenarListaGrupoOcupacaoCargo();

			this.limparCamposOcupacaoCargo();
			
			this.setPossuiAlteracoesPendentes(true);

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void alterar() {

		try {
			// Validar se o cargo já está no grupo de ocupação
			this.custosSigCadastrosBasicosFacade.validarRepeticaoCargosGrupoOcupacao(this.getGrupoOcupacao(), this.getListaCargos(), this.getOcupacaoCargo(),
					this.getIndiceGrupoOcupacaoCargo());

			this.getGrupoOcupacaoCargos().setIndSituacao(this.getSituacaoOcupacaoCargo());
			this.getGrupoOcupacaoCargos().setRapOcupacaoCargo(this.getOcupacaoCargo());
			// Reordenar pela descricao ao adicionar
			this.reordenarListaGrupoOcupacaoCargo();
			this.limparCamposOcupacaoCargo();
			this.setPossuiAlteracoesPendentes(true);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void cancelar() {
		this.limparCamposOcupacaoCargo();
	}

	protected void reordenarListaGrupoOcupacaoCargo() {
		Collections.sort(this.getListaCargos(), new Comparator<SigGrupoOcupacaoCargos>() {
			public int compare(SigGrupoOcupacaoCargos s1, SigGrupoOcupacaoCargos s2) {
				return s1.getRapOcupacaoCargo().getDescricao().compareTo(s2.getRapOcupacaoCargo().getDescricao());
			}
		});

	}

	protected void limparCamposOcupacaoCargo() {
		this.setIndiceGrupoOcupacaoCargo(null);
		this.setOcupacaoCargo(null);
		this.setSituacaoOcupacaoCargo(DominioSituacao.A);
	}

	public void verificarInativacaoGrupoOcupacao() {

		this.setExibirMensagemConfirmacaoInativacao(false);

		// Quando a situação for modificada de ativa para inativa durante a
		// edição de grupo de ocupação
		if (this.getEdicao() && this.getGrupoOcupacao().getIndSituacao() == DominioSituacao.A && this.getSituacao() == DominioSituacao.I) {

			// Então verifica se possui atividades ativas e exibe a mensagem de
			// confirmação
			if (!this.getGrupoOcupacao().getListAtividadePessoas().isEmpty()) {
				for (SigAtividadePessoas atividade : this.getGrupoOcupacao().getListAtividadePessoas()) {
					if (atividade.getIndSituacao() == DominioSituacao.A) {
						this.setExibirMensagemConfirmacaoInativacao(true);
						break;
					}
				}
			}
		}

		// Se não for preciso exibir a mensagem de aviso, então já chama o
		// metódo gravar diretamente
		if (!this.getExibirMensagemConfirmacaoInativacao()) {
			this.gravar();
		}
		else{
			this.openDialog("modalConfirmacaoInativacaoWG");
		}
	}

	public void gravar() {
		try {

			//Valida todos os cargos antes de salvar para caso o centro de custo tenha mudado
			for (int index = 0; index < this.getListaCargos().size(); index++ ) {
				this.custosSigCadastrosBasicosFacade.validarRepeticaoCargosGrupoOcupacao(this.getGrupoOcupacao(), this.getListaCargos(),  this.getListaCargos().get(index).getRapOcupacaoCargo(), index);
			}
			
			
			// Atualiza os dados do objeto que vai ser gravado
			this.getGrupoOcupacao().setDescricao(this.getDescricao());
			this.getGrupoOcupacao().setIndSituacao(this.getSituacao());
			this.getGrupoOcupacao().setFccCentroCustos(this.getCentroCusto());
			this.setListaCargos(this.getListaCargos());

			// Persiste o objeto e faz o flush
			this.custosSigCadastrosBasicosFacade.persistirGrupoOcupacao(this.getGrupoOcupacao());

			// Exibe a mensagem de sucesso da operação
			if (this.getEdicao()) {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_GRUPO_OCUPACAO", this.getGrupoOcupacao().getDescricao());
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_GRUPO_OCUPACAO", this.getGrupoOcupacao().getDescricao());
			}

			// Atualiza o objeto para deixar exatamente como está no banco de
			// dados
			//this.custosSigFacade.refresh(this.getGrupoOcupacao()); TODO

			if (this.getSeqGrupoOcupacao() == null) {
				this.setSeqGrupoOcupacao(this.getGrupoOcupacao().getSeq());
			}
			// Reinia a página para garantir que todos os dados estejam
			// atualizados com o banco
			this.iniciar();

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String verificarAlteracoesPendentes(){
		if(this.getPossuiAlteracoesPendentes()){
			this.openDialog("modalConfirmacaoVoltarWG");
			return null;
		}
		else{
			return this.voltar();
		}
	}
	
	public String voltar() {
		return GRUPO_OCUPACAO_LIST;
	}

	public void limparCentroCusto() {
		this.setCentroCusto(null);
		this.getGrupoOcupacao().setFccCentroCustos(this.getCentroCusto());
	}

	public void selecionarCentroCusto() {
		this.getGrupoOcupacao().setFccCentroCustos(this.getCentroCusto());
	}

	public List<FccCentroCustos> pesquisarCentroCusto(String paramPesquisa) throws BaseException {
		return this.centroCustoFacade.pesquisarCentroCustosPorCentroProdExcluindoGcc(paramPesquisa, null, DominioSituacao.A);
	}

	public List<RapOcupacaoCargo> pesquisarOcupacaoCargo(String paramPesquisa) throws ApplicationBusinessException {
		return cadastrosBasicosFacade.pesquisarOcupacaoCargo((String) paramPesquisa, DominioSituacao.A);
	}


	
	public String adicionarCargoLote(){
		adicionarGrupoOcupacaoEmLoteController.setGrupoOcupacao(this.getGrupoOcupacao());
		return ADICIONAR_GRUPO_OCUPACAO_LOTE;
	}
	
	/**
	 * MEtodo que adiciona os grupos de ocupacao ao cargo, se houver repetidos mostra mensagem ao usuario
	 * @param listCargos
	 */
	public void adicionaCargoEmLote(List<SigGrupoOcupacaoCargos> listCargos){
		boolean isItemRepetido = false;
		
 		for (SigGrupoOcupacaoCargos sigGrupoOcupacaoCargos : listCargos) {
			try {
				this.custosSigCadastrosBasicosFacade.validarRepeticaoCargosGrupoOcupacao(this.getGrupoOcupacao(), this.getListaCargos(), sigGrupoOcupacaoCargos.getRapOcupacaoCargo(), this.getListaCargos().size());
				this.getListaCargos().add(sigGrupoOcupacaoCargos);
			} catch (ApplicationBusinessException e) {
				isItemRepetido = true;
			}
		}
		if(isItemRepetido){
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CARGO_JA_ADICIONADO_LOTE_FAZ_PARTE_CADASTRO");
		}
		
		this.adicionouCargosEmLote = true;
	}
	
	
	public boolean verificarGrupoOcupacaoExiste(){
		return this.getGrupoOcupacao() != null && this.getGrupoOcupacao().getSeq() != null ;
	}
	
	
	// Getters and Setters
	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public Integer getSeqGrupoOcupacao() {
		return seqGrupoOcupacao;
	}

	public void setSeqGrupoOcupacao(Integer seqGrupoOcupacao) {
		this.seqGrupoOcupacao = seqGrupoOcupacao;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}

	public List<SigGrupoOcupacaoCargos> getListaCargos() {
		return listaCargos;
	}

	public void setListaCargos(List<SigGrupoOcupacaoCargos> listaCargos) {
		this.listaCargos = listaCargos;
	}

	public Integer getIndiceGrupoOcupacaoCargo() {
		return indiceGrupoOcupacaoCargo;
	}

	public void setIndiceGrupoOcupacaoCargo(Integer indiceGrupoOcupacaoCargo) {
		this.indiceGrupoOcupacaoCargo = indiceGrupoOcupacaoCargo;
	}

	public RapOcupacaoCargo getOcupacaoCargo() {
		return ocupacaoCargo;
	}

	public void setOcupacaoCargo(RapOcupacaoCargo ocupacaoCargo) {
		this.ocupacaoCargo = ocupacaoCargo;
	}

	public DominioSituacao getSituacaoOcupacaoCargo() {
		return situacaoOcupacaoCargo;
	}

	public void setSituacaoOcupacaoCargo(DominioSituacao situacaoOcupacaoCargo) {
		this.situacaoOcupacaoCargo = situacaoOcupacaoCargo;
	}

	public SigGrupoOcupacoes getGrupoOcupacao() {
		return grupoOcupacao;
	}

	public void setGrupoOcupacao(SigGrupoOcupacoes grupoOcupacao) {
		this.grupoOcupacao = grupoOcupacao;
	}

	public Boolean getExibirMensagemConfirmacaoInativacao() {
		return exibirMensagemConfirmacaoInativacao;
	}

	public void setExibirMensagemConfirmacaoInativacao(Boolean exibirMensagemConfirmacaoInativacao) {
		this.exibirMensagemConfirmacaoInativacao = exibirMensagemConfirmacaoInativacao;
	}

	public SigGrupoOcupacaoCargos getGrupoOcupacaoCargos() {
		return grupoOcupacaoCargos;
	}

	public void setGrupoOcupacaoCargos(SigGrupoOcupacaoCargos grupoOcupacaoCargos) {
		this.grupoOcupacaoCargos = grupoOcupacaoCargos;
	}

	public Boolean getPossuiAlteracoesPendentes() {
		return possuiAlteracoesPendentes;
	}

	public void setPossuiAlteracoesPendentes(Boolean possuiAlteracoesPendentes) {
		this.possuiAlteracoesPendentes = possuiAlteracoesPendentes;
	}
	
	
}
