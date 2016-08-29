package br.gov.mec.aghu.controleinfeccao.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.controleinfeccao.MciBacteriasAssociadasVO;
import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.vo.ResultadoCodificadoExameVO;
import br.gov.mec.aghu.model.MciBacteriaMultir;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroBacteriasMultirController extends ActionController {

	private static final String MENSAGEM_SUCESSO_CADASTRO_BACTERIAS_MULTIR = "MENSAGEM_SUCESSO_CADASTRO_BACTERIAS_MULTIR";
	private static final String PAGE_PESQUISA_BACTERIAS_MULTIR = "pesquisaBacteriasMultir";
	private static final long serialVersionUID = -5159107032113993399L;

	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastroExamesFacade;
	
	// Manter Bactéria
	private Integer seqEditar;
	private Integer codigoManter;
	private String descricaoManter;
	private Boolean ativo;
	private Boolean exibeCodigo;
	private Boolean habilitaDescricao;
	private Date criadoEmEditar;
	private Boolean emEdicao;
	private MciBacteriaMultir itemEdicao;
	private Boolean cadastroNovo;
	
	// suggestion box
	private ResultadoCodificadoExameVO resultadoCodificado = null;
	
	// Manter Bactéria Associada
	private boolean emEdicaoBacteriaAssociada;
	private List<MciBacteriasAssociadasVO> listaBacteriasAssociadas;
	private MciBacteriasAssociadasVO bacteriaAssociada;
	private MciBacteriasAssociadasVO bacteriaAssociadaExclusao;
	
	private String descricaoExclusao;

	private Boolean mostraModalConfirmacaoExclusao;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() {
	
		if(seqEditar != null){
			this.exibeCodigo = true;
			this.emEdicao = true;
			this.mostraModalConfirmacaoExclusao = Boolean.FALSE;
			this.emEdicaoBacteriaAssociada = false;
			this.itemEdicao = controleInfeccaoFacade.obterMciBacteriaMultir(seqEditar);
			this.codigoManter = itemEdicao.getSeq();
			this.descricaoManter = itemEdicao.getDescricao();
			this.ativo = DominioSituacao.A.equals(itemEdicao.getSituacao()) ? true: false;
			this.habilitaDescricao = false;
			this.setListaBacteriasAssociadas(this.controleInfeccaoFacade.listarBacteriasAssociadas(this.seqEditar));
			this.setBacteriaAssociada(new MciBacteriasAssociadasVO());
			this.bacteriaAssociada.setSituacao(Boolean.TRUE);
		}else{
			this.exibeCodigo = false;
			this.emEdicao = false;
			this.codigoManter = null;
			this.descricaoManter = null;
			this.ativo = true;
			this.habilitaDescricao = true;
		}
	}
	
	public String voltarPesquisa(){
		this.emEdicao = null;
		this.seqEditar = null;
		this.itemEdicao = null;
		this.codigoManter = null;
		this.descricaoManter = null;
		this.ativo = null;
		this.habilitaDescricao = null;
		return PAGE_PESQUISA_BACTERIAS_MULTIR;
	}
	
	// Manter Bactéria
	public void inserir() throws ApplicationBusinessException{
		MciBacteriaMultir entity = new MciBacteriaMultir();
		entity.setDescricao(descricaoManter);
		entity.setSituacao(ativo == true ? DominioSituacao.A : DominioSituacao.I);
		entity.setCriadoEm(new Date());
		controleInfeccaoFacade.inserirMciBacteriaMultir(entity);
		
		seqEditar = entity.getSeq();
		inicio();
		
		this.apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_CADASTRO_BACTERIAS_MULTIR, descricaoManter);
	}
	
	public void atualizar() throws ApplicationBusinessException{
		itemEdicao.setSituacao(ativo == true ? DominioSituacao.A : DominioSituacao.I);
		itemEdicao.setAlteradoEm(new Date());
		itemEdicao.setDescricao(descricaoManter);
		controleInfeccaoFacade.atualizarBacteriaMultir(itemEdicao);
		
		seqEditar = itemEdicao.getSeq();
		inicio();
		
		this.apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_CADASTRO_BACTERIAS_MULTIR, descricaoManter);
	}
	
	public void persistirDados(){
		try {
			if(emEdicao){
				atualizar();
			}else{
				inserir();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}	

	// Manter Bactéria Associada
	public void editar(MciBacteriasAssociadasVO item) {
		try{
			this.emEdicaoBacteriaAssociada = true;
			this.setBacteriaAssociada(item);
			
			List<ResultadoCodificadoExameVO> lista = cadastroExamesFacade.buscarResultadosCodificadosPorDescricao(this.bacteriaAssociada.getDescricao());
			
			if(!lista.isEmpty()) {
				this.resultadoCodificado = lista.get(0);	
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}	
	
	public void alterarBacteriaAssociada() throws ApplicationBusinessException {
		try{
			this.bacteriaAssociada.setIndSituacao(this.bacteriaAssociada.getSituacao() ? DominioSituacao.A : DominioSituacao.I);
			this.controleInfeccaoFacade.alterarBacteriaAssociada(this.bacteriaAssociada);
			
			apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_CADASTRO_BACTERIAS_MULTIR,
					this.bacteriaAssociada.getDescricao());
			
			this.cancelarEdicao();
		} catch (ApplicationBusinessException e) {
			this.cancelarEdicao();
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void adicionarBacteriaAssociada() throws ApplicationBusinessException {
		try{
			this.bacteriaAssociada.setMciBacteriaMultir(this.controleInfeccaoFacade.obterBacteriaPorChavePrimaria(seqEditar));
			this.bacteriaAssociada.setDescricao(resultadoCodificado.getRcdDescricao());
			this.bacteriaAssociada.setIndSituacao(this.bacteriaAssociada.getSituacao() ? DominioSituacao.A : DominioSituacao.I);
			this.bacteriaAssociada.setCriadoEm(new Date());
			 
			controleInfeccaoFacade.inserirMciBacteriaMultir(this.bacteriaAssociada);
			this.listaBacteriasAssociadas = this.controleInfeccaoFacade.listarBacteriasAssociadas(this.seqEditar);
			this.apresentarMsgNegocio(Severity.INFO, MENSAGEM_SUCESSO_CADASTRO_BACTERIAS_MULTIR, this.bacteriaAssociada.getDescricao());
			this.cancelarEdicao();
		} catch (ApplicationBusinessException e) {
			this.cancelarEdicao();
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void excluir() {
		try {
			controleInfeccaoFacade.removerBacteriaMultir(bacteriaAssociadaExclusao.getSeq());
			this.setListaBacteriasAssociadas(this.controleInfeccaoFacade.listarBacteriasAssociadas(this.seqEditar));
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_BACTERIA_ASSOCIADA", this.getDescricaoExclusao());
			this.cancelarEdicao();
		} catch (BaseListException e) {
			this.cancelarEdicao();
			apresentarExcecaoNegocio(e);
		} catch (ApplicationBusinessException e) {
			this.cancelarEdicao();
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String cancelar() {
		seqEditar = null;
		codigoManter = null;
		descricaoManter = null;
		exibeCodigo = null;
		emEdicao = null;
		itemEdicao = null;
		resultadoCodificado = null;
		this.listaBacteriasAssociadas = new ArrayList<MciBacteriasAssociadasVO>();
		descricaoExclusao = null;

		return PAGE_PESQUISA_BACTERIAS_MULTIR;
	}	
	
	public void cancelarEdicao() {
		this.emEdicaoBacteriaAssociada = false;
		this.mostraModalConfirmacaoExclusao = Boolean.FALSE;
		this.bacteriaAssociada = new MciBacteriasAssociadasVO();
		this.descricaoExclusao = null;
		this.resultadoCodificado = null;
		this.bacteriaAssociada.setSituacao(Boolean.TRUE);	
	}
	
	// Suggestion Box
	public List<ResultadoCodificadoExameVO> buscarResultadosCodificados(String param) {
		try {
			return this.returnSGWithCount(this.cadastroExamesFacade.buscarResultadosCodificadosBacteriaMultir(param),buscarResultadosCodificadosCount(param));
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_LISTAR_SUGGESTION");
		}
		return null;
	}

	public Long buscarResultadosCodificadosCount(String param) {
		try {
		return cadastroExamesFacade.buscarResultadosCodificadosBacteriaMultirCount((String) param);
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_LISTAR_SUGGESTION");
		}
		return null;
	}	
		
	// ### GETs e SETs ###	
	
	public String getDescricaoManter() {
		return descricaoManter;
	}

	public void setDescricaoManter(String descricaoManter) {
		this.descricaoManter = descricaoManter;
	}

	public Integer getCodigoManter() {
		return codigoManter;
	}

	public void setCodigoManter(Integer codigoManter) {
		this.codigoManter = codigoManter;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public Boolean getEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public Boolean getCadastroNovo() {
		return cadastroNovo;
	}

	public void setCadastroNovo(Boolean cadastroNovo) {
		this.cadastroNovo = cadastroNovo;
	}

	public Boolean getExibeCodigo() {
		return exibeCodigo;
	}

	public void setExibeCodigo(Boolean exibeCodigo) {
		this.exibeCodigo = exibeCodigo;
	}

	public Boolean getHabilitaDescricao() {
		return habilitaDescricao;
	}

	public void setHabilitaDescricao(Boolean habilitaDescricao) {
		this.habilitaDescricao = habilitaDescricao;
	}

	public Integer getSeqEditar() {
		return seqEditar;
	}

	public void setSeqEditar(Integer seqEditar) {
		this.seqEditar = seqEditar;
	}

	public Date getCriadoEmEditar() {
		return criadoEmEditar;
	}

	public void setCriadoEmEditar(Date criadoEmEditar) {
		this.criadoEmEditar = criadoEmEditar;
	}

	public MciBacteriaMultir getItemEdicao() {
		return itemEdicao;
	}

	public void setItemEdicao(MciBacteriaMultir itemEdicao) {
		this.itemEdicao = itemEdicao;
	}

	public List<MciBacteriasAssociadasVO> getListaBacteriasAssociadas() {
		return listaBacteriasAssociadas;
	}

	public void setListaBacteriasAssociadas(List<MciBacteriasAssociadasVO> listaBacteriasAssociadas) {
		this.listaBacteriasAssociadas = listaBacteriasAssociadas;
	}

	public MciBacteriasAssociadasVO getBacteriaAssociada() {
		return bacteriaAssociada;
	}

	public void setBacteriaAssociada(MciBacteriasAssociadasVO bacteriaAssociada) {
		this.bacteriaAssociada = bacteriaAssociada;
	}

	public MciBacteriasAssociadasVO getBacteriaAssociadaExclusao() {
		return bacteriaAssociadaExclusao;
	}

	public void setBacteriaAssociadaExclusao(MciBacteriasAssociadasVO bacteriaAssociadaExclusao) {
		this.bacteriaAssociadaExclusao = bacteriaAssociadaExclusao;
	}
	
	public boolean isEmEdicaoBacteriaAssociada() {
		return emEdicaoBacteriaAssociada;
	}

	public void setEmEdicaoBacteriaAssociada(boolean emEdicaoBacteriaAssociada) {
		this.emEdicaoBacteriaAssociada = emEdicaoBacteriaAssociada;
	}

	public Boolean getMostraModalConfirmacaoExclusao() {
		return mostraModalConfirmacaoExclusao;
	}

	public void setMostraModalConfirmacaoExclusao(
			Boolean mostraModalConfirmacaoExclusao) {
		this.mostraModalConfirmacaoExclusao = mostraModalConfirmacaoExclusao;
	}

	public ResultadoCodificadoExameVO getResultadoCodificado() {
		return resultadoCodificado;
	}

	public void setResultadoCodificado(ResultadoCodificadoExameVO resultadoCodificado) {
		this.resultadoCodificado = resultadoCodificado;
	}

	public String getDescricaoExclusao() {
		return descricaoExclusao;
	}

	public void setDescricaoExclusao(String descricaoExclusao) {
		this.descricaoExclusao = descricaoExclusao;
	}	
	
}
