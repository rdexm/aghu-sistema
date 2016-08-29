package br.gov.mec.aghu.controleinfeccao.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.controleinfeccao.vo.LocaisOrigemInfeccaoVO;
import br.gov.mec.aghu.controleinfeccao.vo.OrigemInfeccoesVO;
import br.gov.mec.aghu.controleinfeccao.vo.TipoGrupoRiscoVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MciProcedimentoRisco;
import br.gov.mec.aghu.model.MciTipoGrupoProcedRisco;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * 
 * @author marcelo.corati
 */

public class CadastroProcedimentoRiscoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3765828490362728206L;
	
	//private static final Log LOG = LogFactory.getLog(CadastroProcedimentoRiscoController.class);
	
	private static final String REDIRECIONA_LISTAR_PROCEDIMENTO_RISCO = "controleinfeccao-pesquisarProcedimentoRisco";

	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	//@EJB
	//private IAghuFacade aghuFacade;
	
	//@Inject
	//private PesquisaOrigemInfeccaoPaginatorController pesquisaOrigemInfeccaoPaginatorController;
	
	private OrigemInfeccoesVO origemSelecionada;
	
	private Boolean ativo;
	
	private AghUnidadesFuncionais unidadeFuncionalPadrao;
	private AghUnidadesFuncionais unidadeFuncionalOrigem;
	
	private List<LocaisOrigemInfeccaoVO> listaLocaisOrigem;
	private LocaisOrigemInfeccaoVO localOrigem;
	private LocaisOrigemInfeccaoVO localOrigemExclusao;
	
	private String descricaoExclusao;
	
	private boolean modoEdicao;
	
	private Boolean mostraModalConfirmacaoExclusao;
	
	private Short codigo;
	
	private String descricao;
	
	private Boolean situacao;
	
	private Short seqEditar;
	
	private Date criadoEmEditar;
	
	private Boolean habilitarDescricao;
	
	private Boolean exibeCodigo;
	
	private MciProcedimentoRisco entityEditar;
	
	private MciTipoGrupoProcedRisco tipoGrupo;
	
	private List<MciTipoGrupoProcedRisco> listaGrupos = new ArrayList<MciTipoGrupoProcedRisco>();
	
	private Short tgpSeqExcluir;
	
	private String descricaoExcluir;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	
	public void inicio() {
	 

	 

		this.tgpSeqExcluir = null;
		this.descricaoExcluir = null;
		this.listaGrupos = new ArrayList<MciTipoGrupoProcedRisco>();
		if(seqEditar != null){
			this.tipoGrupo = null;
			entityEditar = this.controleInfeccaoFacade.obterProcedimentoRisco(this.seqEditar);
			this.codigo = entityEditar.getSeq();
			this.descricao = entityEditar.getDescricao();
			this.situacao = DominioSituacao.A.equals(entityEditar.getIndSituacao()) ? true : false;
			this.habilitarDescricao = false;
			this.exibeCodigo = true;

			// BUSCAR A LISTA DOS GRUPOS ASSOCIADOS
			List<TipoGrupoRiscoVO> listaGruposVO = this.controleInfeccaoFacade.pesquisarMciGrupoProcedRiscoPorSeqeSeqTipoGrupo(seqEditar,null);
			this.listaGrupos = new ArrayList<MciTipoGrupoProcedRisco>();
			for (TipoGrupoRiscoVO item : listaGruposVO) {
				MciTipoGrupoProcedRisco entity = this.controleInfeccaoFacade.obterTipoGrupo(item.getSeq());
				this.listaGrupos.add(entity);
			}
			
		}else{
			this.tipoGrupo = null;
			this.listaGrupos = new ArrayList<MciTipoGrupoProcedRisco>();
			this.codigo = null;
			this.descricao = null;
			this.situacao = true;
			this.habilitarDescricao = true;
			this.exibeCodigo = false;
		}
	
	}
	
	
	public List<MciTipoGrupoProcedRisco> pesquisarSuggestionGrupos(String param) {
		String strPesquisa = (String) param;
		//return this.controleInfeccaoFacade.pesquisarUnidadesAtivas(strPesquisa, true, this.origemSelecionada.getCodigoOrigem());
		List<MciTipoGrupoProcedRisco> sugestion = controleInfeccaoFacade.pesquisarSuggestionGrupos(strPesquisa,seqEditar);
		return sugestion;
	}
	
	public void confirmar() {
		// fluxo de insercao
		if(seqEditar != null){
			try {
				this.controleInfeccaoFacade.atualizaProcedimentoRisco(entityEditar,this.situacao ? DominioSituacao.A : DominioSituacao.I);
				seqEditar = entityEditar.getSeq();
				inicio();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_PR", this.descricao);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}else{
			MciProcedimentoRisco entity = new MciProcedimentoRisco();
			entity.setDescricao(this.descricao);
			entity.setIndSituacao(this.situacao ? DominioSituacao.A : DominioSituacao.I);
			entity.setIndInformacaoHora("N");
			try {
				this.controleInfeccaoFacade.validaeInsereProcedimentoRisco(entity);
				seqEditar = entity.getSeq();
				inicio();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_PR", this.descricao);
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	
	public void adicionarGrupo() {
		if (this.tipoGrupo != null) {
			try {
				
				this.controleInfeccaoFacade.associarGrupoProced(seqEditar, tipoGrupo.getSeq());
				this.listaGrupos.add(this.tipoGrupo);
				this.tipoGrupo = null;
				apresentarMsgNegocio(Severity.INFO, "Grupo incluído com sucesso.");
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		}
	}
	
	public void excluir() {
		try {
			this.mostraModalConfirmacaoExclusao = false;
			this.controleInfeccaoFacade.excluirProcedimentoRisco(this.tgpSeqExcluir,this.seqEditar);
			inicio();
			apresentarMsgNegocio(Severity.INFO, "Grupo removido com sucesso.");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String cancelar() {
		this.codigo = null;
		this.descricao = null;
		this.situacao = null;
		this.seqEditar = null;
		this.tgpSeqExcluir = null;
		this.descricaoExcluir = null;
		this.tipoGrupo = null;
		this.listaGrupos = new ArrayList<MciTipoGrupoProcedRisco>();
		return REDIRECIONA_LISTAR_PROCEDIMENTO_RISCO;
	}
	
	// ### GETs e SETs ###

	public OrigemInfeccoesVO getOrigemSelecionada() {
		return origemSelecionada;
	}

	public void setOrigemSelecionada(OrigemInfeccoesVO origemSelecionada) {
		this.origemSelecionada = origemSelecionada;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public AghUnidadesFuncionais getUnidadeFuncionalPadrao() {
		return unidadeFuncionalPadrao;
	}

	public void setUnidadeFuncionalPadrao(
			AghUnidadesFuncionais unidadeFuncionalPadrao) {
		this.unidadeFuncionalPadrao = unidadeFuncionalPadrao;
	}

	public AghUnidadesFuncionais getUnidadeFuncionalOrigem() {
		return unidadeFuncionalOrigem;
	}

	public void setUnidadeFuncionalOrigem(
			AghUnidadesFuncionais unidadeFuncionalOrigem) {
		this.unidadeFuncionalOrigem = unidadeFuncionalOrigem;
	}

	public List<LocaisOrigemInfeccaoVO> getListaLocaisOrigem() {
		return listaLocaisOrigem;
	}

	public void setListaLocaisOrigem(List<LocaisOrigemInfeccaoVO> listaLocaisOrigem) {
		this.listaLocaisOrigem = listaLocaisOrigem;
	}

	public LocaisOrigemInfeccaoVO getLocalOrigem() {
		return localOrigem;
	}

	public void setLocalOrigem(LocaisOrigemInfeccaoVO localOrigem) {
		this.localOrigem = localOrigem;
	}

	public LocaisOrigemInfeccaoVO getLocalOrigemExclusao() {
		return localOrigemExclusao;
	}

	public void setLocalOrigemExclusao(LocaisOrigemInfeccaoVO localOrigemExclusao) {
		this.localOrigemExclusao = localOrigemExclusao;
	}

	public String getDescricaoExclusao() {
		return descricaoExclusao;
	}

	public void setDescricaoExclusao(String descricaoExclusao) {
		this.descricaoExclusao = descricaoExclusao;
	}

	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public Boolean getMostraModalConfirmacaoExclusao() {
		return mostraModalConfirmacaoExclusao;
	}

	public void setMostraModalConfirmacaoExclusao(
			Boolean mostraModalConfirmacaoExclusao) {
		this.mostraModalConfirmacaoExclusao = mostraModalConfirmacaoExclusao;
	}

	public Short getCodigo() {
		return codigo;
	}

	public void setCodigo(Short codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Boolean getSituacao() {
		return situacao;
	}

	public void setSituacao(Boolean situacao) {
		this.situacao = situacao;
	}

	public Short getSeqEditar() {
		return seqEditar;
	}

	public void setSeqEditar(Short seqEditar) {
		this.seqEditar = seqEditar;
	}

	public Date getCriadoEmEditar() {
		return criadoEmEditar;
	}

	public void setCriadoEmEditar(Date criadoEmEditar) {
		this.criadoEmEditar = criadoEmEditar;
	}

	public Boolean getHabilitarDescricao() {
		return habilitarDescricao;
	}

	public void setHabilitarDescricao(Boolean habilitarDescricao) {
		this.habilitarDescricao = habilitarDescricao;
	}

	public Boolean getExibeCodigo() {
		return exibeCodigo;
	}

	public void setExibeCodigo(Boolean exibeCodigo) {
		this.exibeCodigo = exibeCodigo;
	}

	public MciProcedimentoRisco getEntityEditar() {
		return entityEditar;
	}

	public void setEntityEditar(MciProcedimentoRisco entityEditar) {
		this.entityEditar = entityEditar;
	}

	public MciTipoGrupoProcedRisco getTipoGrupo() {
		return tipoGrupo;
	}

	public void setTipoGrupo(MciTipoGrupoProcedRisco tipoGrupo) {
		this.tipoGrupo = tipoGrupo;
	}

	public List<MciTipoGrupoProcedRisco> getListaGrupos() {
		return listaGrupos;
	}

	public void setListaGrupos(List<MciTipoGrupoProcedRisco> listaGrupos) {
		this.listaGrupos = listaGrupos;
	}

	public Short getTgpSeqExcluir() {
		return tgpSeqExcluir;
	}

	public void setTgpSeqExcluir(Short tgpSeqExcluir) {
		this.tgpSeqExcluir = tgpSeqExcluir;
	}

	public String getDescricaoExcluir() {
		return descricaoExcluir;
	}

	public void setDescricaoExcluir(String descricaoExcluir) {
		this.descricaoExcluir = descricaoExcluir;
	}
	
}
