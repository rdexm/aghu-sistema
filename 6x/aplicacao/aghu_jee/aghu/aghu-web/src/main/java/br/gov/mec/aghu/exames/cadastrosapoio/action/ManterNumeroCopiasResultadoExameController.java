package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.model.AelCopiaResultados;
import br.gov.mec.aghu.model.AelCopiaResultadosId;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterNumeroCopiasResultadoExameController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -3786029617480824142L;

	private static final String PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD = "exames-manterDadosBasicosExamesCRUD";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;

	// Parâmetros da conversação
	private String exaSigla;
	private Integer manSeq;
	private String voltarPara; // O padrão é voltar para interface de pesquisa

	// Variaveis que representam os campos do XHTML
	private AelExamesMaterialAnalise exameMaterialAnalise;
	private AelCopiaResultados copiaResultados;
	private DominioOrigemAtendimento origemAtendimento;
	List<AelCopiaResultados> listaCopiaResultados = new LinkedList<AelCopiaResultados>();
	private AelCopiaResultados parametroSelecionado;

	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void iniciar() {
	 


		if (this.exaSigla != null && manSeq != null) {

			// Instancia uma nova Cópia do Resultado de Exame
			this.copiaResultados = new AelCopiaResultados();

			// Seta o valor padrão "Internação" para origem do atendimento
			this.origemAtendimento = DominioOrigemAtendimento.I;

			// Obtém o Exame Material de Análise
			this.exameMaterialAnalise = this.examesFacade.buscarAelExamesMaterialAnalisePorId(this.exaSigla, this.manSeq);

			// Popula lista de Cópia do Resultado de Exame cadastrados
			this.listaCopiaResultados = this.examesFacade.pesquisarCopiaResultadosPorExameMaterialAnalise(this.exameMaterialAnalise);

		}

	
	}

	/*
	 * Pesquisas para suggestion box
	 */

	/**
	 * Suggestion Box de Convênios
	 * 
	 * @param objPesquisa
	 * @return
	 */
	public List<FatConvenioSaude> pesquisarConvenioSaude(String objPesquisa) {
		final String codDescConvSaude = objPesquisa != null ? StringUtils.trim((String) objPesquisa) : null;
		
		return returnSGWithCount(this.faturamentoApoioFacade.pesquisarConveniosSaudePorCodigoOuDescricaoAtivos(codDescConvSaude),
				this.pesquisarConvenioSaudeCount(codDescConvSaude));
	}
	
	public Long pesquisarConvenioSaudeCount(String objPesquisa) {
		return this.faturamentoApoioFacade.pesquisarConveniosSaudePorCodigoOuDescricaoAtivosCount(objPesquisa);
	}
	
	public Boolean selecionarConvenio(AelCopiaResultados copiaResultadosClicado){
		if(this.copiaResultados.getId() != null && this.copiaResultados.equals(copiaResultadosClicado)){
			return Boolean.TRUE;
		}
		
		return Boolean.FALSE;
	}

	/**
	 * Grava uma Cópia do Resultado de Exame
	 * 
	 * @return
	 */
	public void gravar() {

		// Popula ID de Cópia do Resultado de Exame
		AelCopiaResultadosId id = new AelCopiaResultadosId();
		id.setEmaExaSigla(this.exameMaterialAnalise.getId().getExaSigla());
		id.setEmaManSeq(this.exameMaterialAnalise.getId().getManSeq());
		id.setCnvCodigo(this.copiaResultados.getConvenioSaude().getCodigo());
		id.setOrigemAtendimento(this.origemAtendimento);

		this.copiaResultados.setExamesMaterialAnalise(this.exameMaterialAnalise);

		this.copiaResultados.setId(id);

		try {

			// Persiste Cópia do Resultado de Exame
			this.examesFacade.inserirAelCopiaResultados(this.copiaResultados);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERIR_COPIAS_RESULTADO_EXAMES");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

		this.iniciar();
	}

	/**
	 * Atualiza Cópia do Resultado de Exame alterado na lista de cópias cadastradas
	 * 
	 * @param itemCopiaResultados
	 */
	public void alterar() {

		if (verificarAlteradoOutroUsuario(this.copiaResultados)) {
			return;
		}

		try {
			// Persiste Cópia do Resultado de Exame
			this.examesFacade.atualizarAelCopiaResultados(this.copiaResultados);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERAR_COPIAS_RESULTADO_EXAMES");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		this.iniciar();
	}
	
	public void salvar(){		
		if(this.copiaResultados.getId() != null){
			alterar();
		}else{
			gravar();
		}
	}

	/**
	 * Exclui um item de Cópia do Resultado de Exame através da modal de exclusão
	 */
	public void excluir() {

		try {

			if (verificarAlteradoOutroUsuario(this.parametroSelecionado)) {
				return;
			}

			if (this.parametroSelecionado != null) {
				this.examesFacade.removerAelCopiaResultados(this.parametroSelecionado);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOVER_COPIAS_RESULTADO_EXAMES");
			}

		} catch (BaseException e) {

			apresentarExcecaoNegocio(e);

		} finally {
			this.parametroSelecionado = null;
			this.listaCopiaResultados = null;
		}

		this.iniciar();
	}

	private boolean verificarAlteradoOutroUsuario(AelCopiaResultados entidade) {
		if (entidade == null || this.examesFacade.obterAelCopiaResultadosId(entidade.getId()) == null) {
			apresentarMsgNegocio(Severity.INFO, "REGISTRO_NULO_EXCLUSAO");
			this.listaCopiaResultados = this.examesFacade.pesquisarCopiaResultadosPorExameMaterialAnalise(this.exameMaterialAnalise);
			return true;
		}
		return false;
	}

	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {
		String retorno = this.voltarPara;
		this.limparParametros();
		return retorno;
	}

	/**
	 * Cancela o cadastro
	 */
	public String cancelar() {
		this.limparParametros();
		return PAGE_EXAMES_MANTER_DADOS_BASICOS_EXAMES_CRUD;
	}

	/**
	 * Limpa parâmetros de conversação
	 */
	private void limparParametros() {
		this.exaSigla = null;
		this.manSeq = null;
		this.voltarPara = null;
		this.exameMaterialAnalise = null;
		this.copiaResultados = null;
		this.origemAtendimento = null;
		this.listaCopiaResultados = new LinkedList<AelCopiaResultados>();
		this.parametroSelecionado = null;
	}

	/*
	 * Getters e setters
	 */

	public String getExaSigla() {
		return exaSigla;
	}

	public void setExaSigla(String exaSigla) {
		this.exaSigla = exaSigla;
	}

	public Integer getManSeq() {
		return manSeq;
	}

	public void setManSeq(Integer manSeq) {
		this.manSeq = manSeq;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public AelExamesMaterialAnalise getExameMaterialAnalise() {
		return exameMaterialAnalise;
	}

	public void setExameMaterialAnalise(AelExamesMaterialAnalise exameMaterialAnalise) {
		this.exameMaterialAnalise = exameMaterialAnalise;
	}

	public AelCopiaResultados getCopiaResultados() {
		return copiaResultados;
	}

	public void setCopiaResultados(AelCopiaResultados copiaResultados) {
		this.copiaResultados = copiaResultados;
	}

	public DominioOrigemAtendimento getOrigemAtendimento() {
		return origemAtendimento;
	}

	public void setOrigemAtendimento(DominioOrigemAtendimento origemAtendimento) {
		this.origemAtendimento = origemAtendimento;
	}

	public List<AelCopiaResultados> getListaCopiaResultados() {
		return listaCopiaResultados;
	}

	public void setListaCopiaResultados(List<AelCopiaResultados> listaCopiaResultados) {
		this.listaCopiaResultados = listaCopiaResultados;
	}

	public AelCopiaResultados getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(AelCopiaResultados parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

}