package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.solicitacao.vo.UnfExecutaSinonimoExameVO;
import br.gov.mec.aghu.exames.vo.AelGrpTecnicaUnfExamesVO;
import br.gov.mec.aghu.model.AelGrpTecnicaUnfExames;
import br.gov.mec.aghu.model.AelGrupoExameTecnicas;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterGrupoExamesController extends ActionController {

	private static final long serialVersionUID = -6153762937738535963L;

	private static final String MANTER_GRUPO_EXAMES_PESQUISA = "manterGrupoExamesPesquisa";
	private static final String MANTER_CAMPO_LAUDO_PENDENCIA = "manterCampoLaudoPendencia";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;

	// Variaveis que representam os campos do XHTML
	private AelGrupoExameTecnicas grupoExameTecnicas;
	private Integer codigo;
	private DominioSituacao indSituacao;
	
	
	// Instancia para SuggestionBox de exame
	private UnfExecutaSinonimoExameVO exameVO;
	
	// Lista dos exames (Grupo tecnica de unidade funcional de exames)
	private List<AelGrpTecnicaUnfExamesVO> listaAelGrpTecnicaUnfExamesVO;
	
	// Identificador utilizado para exclusao de um item selecionado na listagem de exames do grupo de exames
	private AelGrpTecnicaUnfExamesVO itemVOExclusao;
	
	//#2219 - parametros necessarios para integracao com a estória
	private Integer grtSeq;
	private String ufeEmaExaSigla;
	private Integer ufeEmaManSeq;
	private Short ufeUnfSeq;
	
	private boolean iniciouTela;
	

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Chamado no inicio de "cada conversacao"
	 */
	public String iniciar() {
	 


		if(iniciouTela){
			return null;
		}
		iniciouTela = true;
		
		if (grupoExameTecnicas != null && grupoExameTecnicas.getSeq() != null) {
			this.grupoExameTecnicas = this.examesFacade.obterAelGrupoExameTecnicasPeloId(grupoExameTecnicas.getSeq()); 

			if(grupoExameTecnicas == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			this.listarAelGrpTecnicaUnfExamesVO();
			
		} else {
			this.grupoExameTecnicas = new AelGrupoExameTecnicas();
			this.listaAelGrpTecnicaUnfExamesVO = new ArrayList<AelGrpTecnicaUnfExamesVO>(0);
		}
		return null;
	
	}
	
	/**
	 * Testa (Forcado) campos obrigatorios em branco
	 */
	private boolean isValidaCamposRequeridosEmBranco(){
		boolean retorno = true;
		if(this.grupoExameTecnicas != null){
			if(StringUtils.isBlank(this.grupoExameTecnicas.getDescricao())){
				retorno = false;
				this.grupoExameTecnicas.setDescricao(null);
				this.apresentarMsgNegocio(Severity.ERROR,"CAMPO_OBRIGATORIO", "Nome");
			} else{
				// Remove espacos em branco
				this.grupoExameTecnicas.setDescricao(StringUtils.trim(this.grupoExameTecnicas.getDescricao()));
			}
		}
		return retorno;
	}
	
	
	/**
	 * Confirma a operacao de gravar/alterar um grupo de exame
	 * @return
	 */
	public String confirmar() {
		
		// Determina o tipo de mensagem de confirmacao
		final boolean isInclusao = this.grupoExameTecnicas.getSeq() == null;
		
		try {
			if(isValidaCamposRequeridosEmBranco()){
				
				// Persiste grupo de exames
				this.cadastrosApoioExamesFacade.persistirAelGrupoExameTecnicas(this.grupoExameTecnicas);
			
				// Determina o tipo de mensagem de confirmacao
				String mensagem = null;
				if (isInclusao) {
					mensagem = "MENSAGEM_SUCESSO_INSERIR_GRUPO_EXAMES";
				} else {
					mensagem = "MENSAGEM_SUCESSO_ALTERAR_GRUPO_EXAMES";
				}
				this.apresentarMsgNegocio(Severity.INFO, mensagem, this.grupoExameTecnicas.getDescricao());

				// Entra em modo de edicao apos a inclusao e exibe a tabela de exames
				if(isInclusao){
					this.codigo = this.grupoExameTecnicas.getSeq();
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} 
		
		return null;
	}
	
	/**
	 * Confirma a insercao/alteracao de um exame no grupo de exames
	 */
	public void confirmarExame(){
		
		try {

			AelGrpTecnicaUnfExames grpTecnicaUnfExames = new AelGrpTecnicaUnfExames();
			
			//setar o provedor do id(chave composta)
			grpTecnicaUnfExames.setAelUnfExecutaExames(this.exameVO.getUnfExecutaExame());
			grpTecnicaUnfExames.setAelGrupoExameTecnicas(this.grupoExameTecnicas);
			
			// Persiste um grupo tecnica de unidade funcional de exames
			this.cadastrosApoioExamesFacade.persistirAelGrpTecnicaUnfExames(grpTecnicaUnfExames);

			// Refaz a pesquisa de grupo tecnica de unidade funcional de exames
			this.listarAelGrpTecnicaUnfExamesVO();
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERIR_GRUPO_TECNICA_UNF_EXAMES", this.exameVO.getDescricaoExameFormatada());
			
			this.exameVO = null;
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Busca os exames do grupo de exames
	 */
	public void listarAelGrpTecnicaUnfExamesVO(){
		this.listaAelGrpTecnicaUnfExamesVO = this.cadastrosApoioExamesFacade.buscarAelGrpTecnicaUnfExamesVOPorAelGrupoExameTecnica(this.grupoExameTecnicas);
	}
	
	public void excluirExame()  {
		try {
			this.cadastrosApoioExamesFacade.removerAelGrpTecnicaUnfExames(this.itemVOExclusao.getId());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUIR_GRUPO_TECNICA_UNF_EXAMES", this.itemVOExclusao.getDescricaoExame());
		} catch (BaseListException e) {
			for (Iterator<BaseException> errors = e.iterator(); errors.hasNext();) {
				BaseException aghuNegocioException = (BaseException) errors.next();
				super.apresentarExcecaoNegocio(aghuNegocioException);	
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			
		} finally{
			this.itemVOExclusao = null;
			this.listarAelGrpTecnicaUnfExamesVO();
		}
	}

	/**
	 * Cancela a insercao ou alteracao na tela
	 */
	public String cancelar() {
		this.grupoExameTecnicas = null;
		this.codigo = null;
		grupoExameTecnicas = null;
		iniciouTela = false;
        this.exameVO = null;
		return MANTER_GRUPO_EXAMES_PESQUISA;
	}

	/**
	 * Metodo utilizado pela suggestionbox para pesquisa de exames
	 */
	public List<UnfExecutaSinonimoExameVO> obterExames(String nomeExame) {
		return this.solicitacaoExameFacade.pesquisaUnidadeExecutaSinonimoExameAntigo((String) nomeExame);
	}

	public String chamarTelaCampoLaudoPendencia() {
		return MANTER_CAMPO_LAUDO_PENDENCIA;
	}
	
	
	/*
	 * Getters e setters
	 */
	public AelGrupoExameTecnicas getGrupoExameTecnicas() {
		return grupoExameTecnicas;
	}
	
	public void setGrupoExameTecnicas(AelGrupoExameTecnicas grupoExameTecnicas) {
		this.grupoExameTecnicas = grupoExameTecnicas;
	}
	
	public Integer getCodigo() {
		return codigo;
	}
	
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}
	
	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	public UnfExecutaSinonimoExameVO getExameVO() {
		return exameVO;
	}
	
	public void setExameVO(UnfExecutaSinonimoExameVO exameVO) {
		this.exameVO = exameVO;
	}

	public List<AelGrpTecnicaUnfExamesVO> getListaAelGrpTecnicaUnfExamesVO() {
		return listaAelGrpTecnicaUnfExamesVO;
	}
	
	public void setListaAelGrpTecnicaUnfExamesVO(List<AelGrpTecnicaUnfExamesVO> listaAelGrpTecnicaUnfExamesVO) {
		this.listaAelGrpTecnicaUnfExamesVO = listaAelGrpTecnicaUnfExamesVO;
	}

	public AelGrpTecnicaUnfExamesVO getItemVOExclusao() {
		return itemVOExclusao;
	}
	
	public void setItemVOExclusao(AelGrpTecnicaUnfExamesVO itemVOExclusao) {
		this.itemVOExclusao = itemVOExclusao;
	}

	public Integer getGrtSeq() {
		return grtSeq;
	}

	public void setGrtSeq(Integer grtSeq) {
		this.grtSeq = grtSeq;
	}

	public String getUfeEmaExaSigla() {
		return ufeEmaExaSigla;
	}

	public void setUfeEmaExaSigla(String ufeEmaExaSigla) {
		this.ufeEmaExaSigla = ufeEmaExaSigla;
	}

	public Integer getUfeEmaManSeq() {
		return ufeEmaManSeq;
	}

	public void setUfeEmaManSeq(Integer ufeEmaManSeq) {
		this.ufeEmaManSeq = ufeEmaManSeq;
	}

	public Short getUfeUnfSeq() {
		return ufeUnfSeq;
	}

	public void setUfeUnfSeq(Short ufeUnfSeq) {
		this.ufeUnfSeq = ufeUnfSeq;
	}

	public boolean isIniciouTela() {
		return iniciouTela;
	}

	public void setIniciouTela(boolean iniciouTela) {
		this.iniciouTela = iniciouTela;
	}
}
