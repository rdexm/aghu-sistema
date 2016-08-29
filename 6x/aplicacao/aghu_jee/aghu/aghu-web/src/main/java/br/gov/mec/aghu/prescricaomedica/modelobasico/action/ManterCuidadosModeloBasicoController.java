package br.gov.mec.aghu.prescricaomedica.modelobasico.action;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmModeloBasicoCuidado;
import br.gov.mec.aghu.model.MpmModeloBasicoCuidadoId;
import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.prescricaomedica.modelobasico.business.IModeloBasicoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterCuidadosModeloBasicoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4636293523523081973L;

	private static final String PAGE_PRESCRICAO_MEDICA_MANTER_ITENS_MODELO_BASICO = "prescricaomedica-manterItensModeloBasico";

	@EJB
	private IModeloBasicoFacade modeloBasicoFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	// chave do modelo de cuidado recebida via parametro
	private Integer modeloBasicoPrescricaoSeq;
	private Integer seq;

	private MpmModeloBasicoPrescricao modeloBasico = new MpmModeloBasicoPrescricao();
	private List<MpmModeloBasicoCuidado> listaCuidados = new ArrayList<MpmModeloBasicoCuidado>();

	private MpmModeloBasicoCuidado modeloBasicoCuidado;

	private boolean alterar = false;

	// Utilizado na frequencia
	private Integer frequencia;
	private MpmTipoFrequenciaAprazamento tipoAprazamento;

	private boolean confirmaVoltar;

	// Campos para o controle de alteração no formulário do item
	private boolean campoAlteradoFormularioItem;
	private Integer seqEdicao;

	private enum ManterCuidadosModeloBasicoControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_MODELO_NAO_INFORMADO, MENSAGEM_TIPO_NAO_EXISTE, MENSAGEM_MODELO_NAO_EXISTE;
	}

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
	 

		try {
			if (modeloBasicoPrescricaoSeq == null) {
				throw new ApplicationBusinessException(ManterCuidadosModeloBasicoControllerExceptionCode.MENSAGEM_MODELO_NAO_INFORMADO);
			}

			this.modeloBasico = this.modeloBasicoFacade.obterModeloBasico(modeloBasicoPrescricaoSeq);

			if (this.modeloBasico == null) {
				throw new ApplicationBusinessException(ManterCuidadosModeloBasicoControllerExceptionCode.MENSAGEM_MODELO_NAO_EXISTE);
			}

			this.obterListaCuidados();

			// verifica se é inclusão ou alteração
			if (seq == null) {
				this.modeloBasicoCuidado = new MpmModeloBasicoCuidado();
				this.modeloBasicoCuidado.setModeloBasicoPrescricao(modeloBasico);
				this.modeloBasicoCuidado.setServidor(this.servidorLogadoFacade.obterServidorLogado());
				this.modeloBasicoCuidado.setModeloBasicoPrescricao(modeloBasico);

			} else {
				this.preparaAlterar(modeloBasicoPrescricaoSeq, seq);
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e); // trata erro de parâmetro de sistema
		}
	
	}

	/**
	 * Carrega campos com os valores default do cuidado usual apos selecionar um
	 * cuidado usual na suggestion
	 */
	public void carregaCampos() {

		this.marcarAlteracaoCampoFormularioItem();

		if (this.modeloBasicoCuidado.getCuidadoUsual() != null) {
			if (this.modeloBasicoCuidado.getCuidadoUsual().getFrequencia() != null) {
				this.frequencia = this.modeloBasicoCuidado.getCuidadoUsual().getFrequencia().intValue();
			}

			if (this.modeloBasicoCuidado.getCuidadoUsual().getMpmTipoFreqAprazamentos() != null) {
				if (this.modeloBasicoCuidado.getCuidadoUsual().getMpmTipoFreqAprazamentos() != null) {
					this.tipoAprazamento = this.modeloBasicoCuidado.getCuidadoUsual().getMpmTipoFreqAprazamentos();
				}
			}
		}
	}

	public List<MpmCuidadoUsual> obterListaCuidadoUsual(String cuidadoUsualPesquisa) {
		return this.returnSGWithCount(this.modeloBasicoFacade.obterListaCuidadosUsuais(cuidadoUsualPesquisa),this.modeloBasicoFacade.obterListaCuidadoUsualCount(cuidadoUsualPesquisa));
	}

	public void preparaAlterar(Integer modeloBasicoPrescricaoSeq, Integer seq) {

		this.modeloBasicoPrescricaoSeq = modeloBasicoPrescricaoSeq;
		this.seqEdicao = seq;

		if (!this.isCampoAlteradoFormularioItem()) {
			this.editarItem();
		}
	}

	public void editarItem() {

		this.limpar();
		Integer modeloBasicoPrescricaoSeq = this.modeloBasicoPrescricaoSeq;
		Integer seq = this.seqEdicao;
		this.seq = seq;

		if (this.seq != null) {

			MpmModeloBasicoCuidadoId id = new MpmModeloBasicoCuidadoId(modeloBasicoPrescricaoSeq, seq);
			this.modeloBasicoCuidado = this.modeloBasicoFacade.obterItemCuidado(id);
			this.modeloBasicoCuidado.setModeloBasicoPrescricao(modeloBasico);
			this.modeloBasicoCuidado.setServidor(this.servidorLogadoFacade.obterServidorLogado());

			// popula frequencia , siglaTipoAprazamento
			if (this.modeloBasicoCuidado.getFrequencia() != null) {
				this.frequencia = this.modeloBasicoCuidado.getFrequencia();
			} else {
				this.frequencia = null;
			}

			if (this.modeloBasicoCuidado.getTipoFrequenciaAprazamento() != null) {
				this.tipoAprazamento = this.modeloBasicoCuidado.getTipoFrequenciaAprazamento();
			} else {
				this.tipoAprazamento = null;
			}
			this.alterar = true;
		}
	}

	/**
	 * Retorna lista de cuidados de determinado modelo
	 */
	public void obterListaCuidados() {
		this.listaCuidados = this.modeloBasicoFacade.obterListaCuidados(this.modeloBasicoPrescricaoSeq);
		this.ordenaLista(this.listaCuidados);
	}
	
	/**
	 * Retorna a descrição editada de um modelo de cuidado
	 * 
	 * @param modeloBasicoCuidado
	 * @return
	 */
	public String obterDescricaoEditada(Integer modeloBasicoPrescricaoSeq, Integer seq) {
		String retorno = null;
		MpmModeloBasicoCuidado modeloBasicoCuidado = this.modeloBasicoFacade.obterItemCuidado(new MpmModeloBasicoCuidadoId(modeloBasicoPrescricaoSeq, seq));
		if (modeloBasicoCuidado != null) {
			retorno = this.modeloBasicoFacade.obterDescricaoEditadaModeloBasicoCuidado(modeloBasicoCuidado);
		}
		return retorno;
	}

	/**
	 * ordena lista de cuidados alfabeticamente, considerando acentos na
	 * comparacao
	 * 
	 * @param listaCuidados
	 */
	private void ordenaLista(List<MpmModeloBasicoCuidado> listaCuidados) {
		final Collator collator = Collator.getInstance(new Locale("pt", "BR"));
		collator.setStrength(Collator.PRIMARY);
		Collections.sort(listaCuidados, new Comparator<MpmModeloBasicoCuidado>() {
			public int compare(MpmModeloBasicoCuidado mpmModeloBasicoCuidado, MpmModeloBasicoCuidado mpmModeloBasicoCuidado1) {
				return collator.compare(modeloBasicoFacade.obterDescricaoEditadaModeloBasicoCuidado(mpmModeloBasicoCuidado), modeloBasicoFacade.obterDescricaoEditadaModeloBasicoCuidado(mpmModeloBasicoCuidado1));
			}
		});
	}

	/**
	 * Salva inclusão ou alteração de registro.
	 */
	public void salvar() {
		try {
			if (this.tipoAprazamento == null) {
				apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Tipo de Aprazamento");
				return;
			} else if (this.verificaRequiredFrequencia() && this.frequencia == null) {
				apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Frequência");
				return;
			} else {
				this.modeloBasicoCuidado.setModeloBasicoPrescricao(modeloBasico);
				this.modeloBasicoCuidado.setTipoFrequenciaAprazamento(this.tipoAprazamento);

				if (this.frequencia != null) {
					this.modeloBasicoCuidado.setFrequencia(this.frequencia.intValue());
				} else {
					this.modeloBasicoCuidado.setFrequencia(null);
				}

				if (isAlterar()) {
					this.modeloBasicoFacade.alterar(this.modeloBasicoCuidado);
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_CUIDADO_MODELO_BASICO", this.modeloBasicoFacade.obterDescricaoEditadaModeloBasicoCuidado(modeloBasicoCuidado));

				} else {
					this.modeloBasicoFacade.inserir(this.modeloBasicoCuidado);
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_CUIDADO_MODELO_BASICO", this.modeloBasicoFacade.obterDescricaoEditadaModeloBasicoCuidado(modeloBasicoCuidado));
				}

				this.limpar();
				this.iniciar();
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}


	}

	/**
	 * Método para limpar o formulário da tela.
	 */
	public void limpar() {

		this.seq = null;
		this.modeloBasicoCuidado = new MpmModeloBasicoCuidado();
		this.frequencia = null;
		this.alterar = false;
		this.tipoAprazamento = null;
		this.confirmaVoltar = false;
		this.desmarcarAlteracaoCampoFormularioItem();
	}

	public String voltar() {
		limpar();
		return PAGE_PRESCRICAO_MEDICA_MANTER_ITENS_MODELO_BASICO;
	}

	public String verificaPendencias() {
		if (this.modeloBasicoCuidado.getCuidadoUsual() != null) {
			confirmaVoltar = true;
			return null;
		} else {
			confirmaVoltar = false;
			return PAGE_PRESCRICAO_MEDICA_MANTER_ITENS_MODELO_BASICO;
		}
	}

	/**
	 * Exclui um cuidado básico
	 * 
	 * @param modeloBasicoPrescricaoSeq
	 * @param seq
	 */

	public void excluir(MpmModeloBasicoCuidado modelo) {
		try {
			this.modeloBasicoCuidado = modeloBasicoFacade.obterModeloBasicoCuidado(modelo.getId().getModeloBasicoPrescricaoSeq(), modelo.getId().getSeq());
			final String descricao = this.modeloBasicoFacade.obterDescricaoEditadaModeloBasicoCuidado(this.modeloBasicoCuidado);
			this.modeloBasicoFacade.excluir(this.modeloBasicoCuidado);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_ITEM_CUIDADO", descricao);
			this.limpar();
			this.iniciar();

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void cancelarModal(){
		this.seq = null;
	}

	public List<MpmTipoFrequenciaAprazamento> buscarTiposFrequenciaAprazamento(String strPesquisa) {
		return this.returnSGWithCount(this.modeloBasicoFacade.obterListaTipoFrequenciaAprazamento(strPesquisa),this.modeloBasicoFacade.obterListaTipoFrequenciaAprazamentoCount(strPesquisa));
	}

	public String getDescricaoTipoFrequenciaAprazamento() {
		return buscaDescricaoTipoFrequenciaAprazamento(this.tipoAprazamento);
	}

	public String buscaDescricaoTipoFrequenciaAprazamento(MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		return tipoFrequenciaAprazamento != null ? tipoFrequenciaAprazamento.getDescricaoSintaxeFormatada(this.frequencia != null ? this.frequencia.shortValue() : null) : "";
	}

	public boolean verificaRequiredFrequencia() {
		return this.tipoAprazamento != null && this.tipoAprazamento.getIndDigitaFrequencia();
	}

	public void verificarFrequencia() {

		this.marcarAlteracaoCampoFormularioItem();

		if (!this.verificaRequiredFrequencia()) {
			this.frequencia = null;
		}
	}

	public void marcarAlteracaoCampoFormularioItem() {
		this.setCampoAlteradoFormularioItem(true);
	}

	public void desmarcarAlteracaoCampoFormularioItem() {
		this.setCampoAlteradoFormularioItem(false);
	}

	// getters & setters
	public IModeloBasicoFacade getModeloBasicoFacade() {
		return modeloBasicoFacade;
	}

	public void setModeloBasicoFacade(IModeloBasicoFacade modeloBasico) {
		this.modeloBasicoFacade = modeloBasico;
	}

	public Integer getModeloBasicoPrescricaoSeq() {
		return modeloBasicoPrescricaoSeq;
	}

	public void setModeloBasicoPrescricaoSeq(Integer modeloBasicoPrescricaoSeq) {
		this.modeloBasicoPrescricaoSeq = modeloBasicoPrescricaoSeq;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public MpmModeloBasicoPrescricao getModeloBasico() {
		return modeloBasico;
	}

	public void setModeloBasico(MpmModeloBasicoPrescricao modeloBasico) {
		this.modeloBasico = modeloBasico;
	}

	public MpmModeloBasicoCuidado getModeloBasicoCuidado() {
		return modeloBasicoCuidado;
	}

	public void setModeloBasicoCuidado(MpmModeloBasicoCuidado modeloBasicoCuidado) {
		this.modeloBasicoCuidado = modeloBasicoCuidado;
	}

	public boolean isAlterar() {
		return alterar;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}

	public List<MpmModeloBasicoCuidado> getListaCuidados() {
		return listaCuidados;
	}

	public void setListaCuidados(List<MpmModeloBasicoCuidado> listaCuidados) {
		this.listaCuidados = listaCuidados;
	}

	public Integer getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Integer frequencia) {
		this.frequencia = frequencia;
	}

	public boolean isConfirmaVoltar() {
		return confirmaVoltar;
	}

	public void setConfirmaVoltar(boolean confirmaVoltar) {
		this.confirmaVoltar = confirmaVoltar;
	}

	public MpmTipoFrequenciaAprazamento getTipoAprazamento() {
		return tipoAprazamento;
	}

	public void setTipoAprazamento(MpmTipoFrequenciaAprazamento tipoAprazamento) {
		this.tipoAprazamento = tipoAprazamento;
	}

	public void setCampoAlteradoFormularioItem(boolean campoAlteradoFormularioItem) {
		this.campoAlteradoFormularioItem = campoAlteradoFormularioItem;
	}

	public boolean isCampoAlteradoFormularioItem() {
		return campoAlteradoFormularioItem;
	}
}
