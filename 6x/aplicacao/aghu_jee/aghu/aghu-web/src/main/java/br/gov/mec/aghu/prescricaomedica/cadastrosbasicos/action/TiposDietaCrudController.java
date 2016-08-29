package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioRestricao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AnuTipoItemDieta;
import br.gov.mec.aghu.model.AnuTipoItemDietaUnfs;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.nutricao.business.INutricaoFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.modelobasico.business.IModeloBasicoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class TiposDietaCrudController extends ActionController {

	private static final long serialVersionUID = 2268130714768982109L;

	private static final String PAGE_PESQUISA_TIPOS_DIETA = "tiposDietaList";

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IModeloBasicoFacade modeloBasicoFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;

	@EJB
	private INutricaoFacade nutricaoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	TiposDietaPaginatorController tiposDietaPaginatorController;

	private AnuTipoItemDieta tipoDieta;

	private MpmUnidadeMedidaMedica unidadeMedida;
	private MpmTipoFrequenciaAprazamento tipoAprazamento;

	/* Unidades funcionais */
	private List<AghUnidadesFuncionais> listaUnidadesFuncionais = new ArrayList<AghUnidadesFuncionais>();
	private AghUnidadesFuncionais unidadeFuncional;
	private AghUnidadesFuncionais unidadeFuncionalSelecionada;
	public static final UnidadeFuncionalComparator UNIDADE_COMPARATOR = new UnidadeFuncionalComparator();

	private Integer ainTipoDietaCodigo;

	private boolean desabilitarCodigo;
	private boolean hiddenCodigo;
	private boolean exigeComplemento;
	private boolean outros;

	private List<AnuTipoItemDietaUnfs> listaUnidadeFuncAdicionadas;

	private List<AnuTipoItemDietaUnfs> listaExcluiUnidadeFunc;

	private List<AnuTipoItemDietaUnfs> tipoDietaUnidadeFuncionais;

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	public void iniciar() {
		listaUnidadeFuncAdicionadas = new ArrayList<AnuTipoItemDietaUnfs>();
		listaExcluiUnidadeFunc = new ArrayList<AnuTipoItemDietaUnfs>();
		
		if(getTipoDietaUnidadeFuncionais() == null){
			setTipoDietaUnidadeFuncionais(new ArrayList<AnuTipoItemDietaUnfs>());
		}
		
		if (this.ainTipoDietaCodigo != null) {
			this.tipoDieta = nutricaoFacade.obterTipoDietaEdicao(this.ainTipoDietaCodigo);
			this.hiddenCodigo = true;
			if (this.tipoDieta != null) {
				setTipoDietaUnidadeFuncionais(this.nutricaoFacade.obterUnfsPorTipoDietaSeq(this.tipoDieta));
				for (AnuTipoItemDietaUnfs tipoDietaUnidadeFuncional : getTipoDietaUnidadeFuncionais()) {
					AghUnidadesFuncionais vo = new AghUnidadesFuncionais();
					vo.setSeq(tipoDietaUnidadeFuncional.getUnidadeFuncional().getSeq());
					vo.setDescricao(tipoDietaUnidadeFuncional.getUnidadeFuncional().getDescricao());
					vo.setAndar(tipoDietaUnidadeFuncional.getUnidadeFuncional().getAndar());
					vo.setIndAla(tipoDietaUnidadeFuncional.getUnidadeFuncional().getIndAla());
					this.listaUnidadesFuncionais.add(vo);
				}
				this.unidadeMedida = this.tipoDieta.getUnidadeMedidaMedica();
				this.tipoAprazamento = this.tipoDieta.getTipoFrequenciaAprazamento();
			}
		} else {
			this.tipoDieta = new AnuTipoItemDieta();
			this.tipoDieta.setIndDigitaAprazamento(DominioRestricao.N);
			this.tipoDieta.setIndDigitaQuantidade(DominioRestricao.N);
		}
	
	}

	/**
	 * Método que realiza a ação do botão confirmar na tela de cadastro de
	 * unidade de medida medica
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */
	public String confirmar() throws ApplicationBusinessException {
		// String retorno = "erro";
		if ((StringUtils.isBlank(this.tipoDieta.getDescricao()) && this.tipoDieta.getDescricao().length() > 0)
				|| (StringUtils.isBlank(this.tipoDieta.getSintaxeMedico()) && this.tipoDieta.getSintaxeMedico().length() > 0)
				|| (StringUtils.isBlank(this.tipoDieta.getSintaxeNutricao()) && this.tipoDieta.getSintaxeNutricao().length() > 0)) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CAMPO_BLANK");
			return null;
		}
		trim();
		try {

			if (this.tipoAprazamento == null
					&& (!this.tipoDieta.getIndDigitaAprazamento().equals(DominioRestricao.N) && !this.tipoDieta.getIndDigitaAprazamento().equals(DominioRestricao.C))) {
				this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Tipo de Aprazamento");
				return null;
			} else if (this.verificaRequiredFrequencia() && this.tipoDieta.getFrequencia() == null) {
				this.apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Frequência");
				return null;
			} else if (DominioRestricao.N.equals(tipoDieta.getIndDigitaQuantidade()) && this.unidadeMedida != null) {
				this.apresentarMsgNegocio(Severity.ERROR, "A Unidade só deve ser informada quando o tipo de dieta possuír digitação de quantidade obrigatória ou opcional");
				return null;
			} else {
				// Tarefa 659 - deixar todos textos das entidades em caixa
				// alta via toUpperCase()
				transformarTextosCaixaAlta();

				boolean create = this.tipoDieta.getSeq() == null;

				tipoDieta.setTipoFrequenciaAprazamento(this.tipoAprazamento);
				tipoDieta.setUnidadeMedidaMedica(this.unidadeMedida);
				cadastrosBasicosPrescricaoMedicaFacade.persistirTiposDieta(tipoDieta, listaUnidadeFuncAdicionadas, listaExcluiUnidadeFunc);

				if (create) {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_TIPO_ITEM_DIETA", this.tipoDieta.getDescricao());
				} else {
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_TIPO_ITEM_DIETA", this.tipoDieta.getDescricao());
				}

				this.desabilitarCodigo = false;
				this.hiddenCodigo = false;
				this.tipoDieta = null;
				this.ainTipoDietaCodigo = null;
				this.listaUnidadesFuncionais = new ArrayList<AghUnidadesFuncionais>();
				this.unidadeFuncional = null;
				this.unidadeMedida = null;
				this.tipoAprazamento = null;
				this.unidadeFuncionalSelecionada = null;
				this.listaUnidadeFuncAdicionadas = null;
				this.listaExcluiUnidadeFunc = null;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		return PAGE_PESQUISA_TIPOS_DIETA;
	}

	private void trim() {
		if (this.tipoDieta.getDescricao() != null) {
			this.tipoDieta.setDescricao(this.tipoDieta.getDescricao().trim());
		}
		if (this.tipoDieta.getDescricaoCompletaMapaDieta() != null) {
			this.tipoDieta.setDescricaoCompletaMapaDieta(this.tipoDieta.getDescricaoCompletaMapaDieta().trim());
		}
		if (this.tipoDieta.getSintaxeMedico() != null) {
			this.tipoDieta.setSintaxeMedico(this.tipoDieta.getSintaxeMedico().trim());
		}
		if (this.tipoDieta.getSintaxeNutricao() != null) {
			this.tipoDieta.setSintaxeNutricao(this.tipoDieta.getSintaxeNutricao().trim());
		}
	}

	/**
	 * Retorna true se o campo aprazamento deve ser desabilidado.
	 * 
	 * @return
	 */
	public boolean verificaDisabledAprazamento() {
		if (this.tipoDieta != null && DominioRestricao.N.equals(tipoDieta.getIndDigitaAprazamento())) {

			tipoAprazamento = null; // limpa
			this.tipoDieta.setFrequencia(null);
			return true; // desabilitado
		} else {

			// tipoAprazamento = null; // limpa
			return false; // habilitado
		}
	}

	/**
	 * Retorna true se o campo tipo aprazamento é obrigatório.
	 * 
	 * @return
	 */
	public boolean verificaRequiredTipoAprazamento() {
		if (this.tipoDieta != null && DominioRestricao.O.equals(tipoDieta.getIndDigitaAprazamento())) {
			return true; // requerido
		} else {
			return false; // não requerido
		}

	}

	public boolean verificaRequiredUnidade() {
		if (this.tipoDieta != null && DominioRestricao.N.equals(this.tipoDieta.getIndDigitaQuantidade())) {
			// unidadeMedida = null; // limpa
			return false; // não obrigatorio
		} else {
			// unidadeMedida = null; // limpa
			return true; // obrigatorio
		}
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de
	 * Acomodação
	 */
	public String cancelar() {
		this.tipoDieta = null;
		this.ainTipoDietaCodigo = null;
		this.desabilitarCodigo = false;
		this.hiddenCodigo = false;
		this.unidadeMedida = null;
		this.tipoAprazamento = null;
		this.listaUnidadesFuncionais = new ArrayList<AghUnidadesFuncionais>();
		this.unidadeFuncional = null;
		return PAGE_PESQUISA_TIPOS_DIETA;
	}

	/**
	 * Retorna os tipos de itens de dieta.
	 * 
	 * @param itemDietaPesquisa
	 * @return
	 */
	public List<MpmUnidadeMedidaMedica> obterUnidadesMedidasMedicas(String idOuDescricao) {
		return this.cadastrosBasicosPrescricaoMedicaFacade.pesquisarUnidadesMedidaMedica(idOuDescricao);
	}

	public String getDescricaoTipoFrequenciaAprazamento() {
		return buscaDescricaoTipoFrequenciaAprazamento(this.tipoAprazamento);
	}

	public String buscaDescricaoTipoFrequenciaAprazamento(MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		return tipoFrequenciaAprazamento != null ? tipoFrequenciaAprazamento.getDescricaoSintaxeFormatada(this.tipoDieta.getFrequencia()) : "";
	}

	public List<MpmTipoFrequenciaAprazamento> buscarTiposFrequenciaAprazamento(String strPesquisa) {
		return this.prescricaoMedicaFacade.buscarTipoFrequenciaAprazamento((String) strPesquisa);
	}

	/**
	 * Limpa o formulario e retorna para modo de inclusão.
	 */
	public void limpar() {
		this.unidadeMedida = null;
	}

	private void transformarTextosCaixaAlta() {
		this.tipoDieta.setDescricao(this.tipoDieta.getDescricao() == null ? null : this.tipoDieta.getDescricao().toUpperCase());
	}

	public boolean verificaRequiredFrequencia() {
		return this.tipoAprazamento != null && this.tipoAprazamento.getIndDigitaFrequencia();
	}

	public void verificarFrequencia() {
		if (!this.verificaRequiredFrequencia()) {
			this.tipoDieta.setFrequencia(null);
		}
	}

	/**
	 * Método da suggestion box para pesquisa de unidades funcionais a incluir
	 * na lista Exclui da listagem os itens que já estão na tela Ignora a
	 * pesquisa caso o parametro seja o próprio valor selecionado anteriormente
	 * (contorna falha de pesquisa múltipla na suggestion box)
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionais(String parametro) {
		String paramString = (String) parametro;
		Set<AghUnidadesFuncionais> result = new HashSet<AghUnidadesFuncionais>();
		if ((unidadeFuncionalSelecionada == null)
				|| !(StringUtils.equalsIgnoreCase(paramString, String.valueOf(unidadeFuncionalSelecionada.getSeq())) || StringUtils.equalsIgnoreCase(paramString,
						unidadeFuncionalSelecionada.getLPADAndarAlaDescricao()))) {
			result = new HashSet<AghUnidadesFuncionais>(aghuFacade.pesquisarAghUnidadesFuncionaisPorCodigoDescricao(parametro, true));
			result.removeAll(getListaUnidadesFuncionais());
		} else {
			// adiciona a selecionada para nao mostrar mensagens erradas na tela
			result.add(unidadeFuncionalSelecionada);
		}
		List<AghUnidadesFuncionais> resultReturn = new ArrayList<AghUnidadesFuncionais>(result);
		Collections.sort(resultReturn, UNIDADE_COMPARATOR);
		return resultReturn;
	}

	public void selecionouUnidadeFuncional() {
		setUnidadeFuncionalSelecionada(getUnidadeFuncional());
	}

	/**
	 * Adiciona uma equipe na lista em memória Caso a especialidade já esteja na
	 * lista, ou seja nula, ignora
	 * @throws ApplicationBusinessException 
	 */
	public void adicionarUnidadeFuncional() throws ApplicationBusinessException {
		
		AnuTipoItemDietaUnfs tipoDietaUnfs = new AnuTipoItemDietaUnfs();
		
		if (getUnidadeFuncionalSelecionada() == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_UNIDADE_FUNCIONAL_TIPO_DIETA_OBRIGATORIA");
			return;
		}

		if (getTipoDietaUnidadeFuncionais() != null && !getTipoDietaUnidadeFuncionais().contains(getUnidadeFuncionalSelecionada())) {
			Collections.sort(listaUnidadesFuncionais, UNIDADE_COMPARATOR);
			tipoDietaUnfs.setCriadoEm(new Date());
			tipoDietaUnfs.setServidor(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
			tipoDietaUnfs.setTipoItemDieta(tipoDieta);
			tipoDietaUnfs.setUnidadeFuncional(unidadeFuncional);
			getListaUnidadeFuncAdicionadas().add(tipoDietaUnfs);
			tipoDietaUnidadeFuncionais.add(tipoDietaUnfs);
			setUnidadeFuncional(null);
			setUnidadeFuncionalSelecionada(null);
		}
	}

	/**
	 * Método que exclui uma unidade funcional da lista em memória Ignora nulos
	 * 
	 * @param espParaExcluir
	 */
	public void excluirUnidadeFuncional(AnuTipoItemDietaUnfs unidade) {
		getTipoDietaUnidadeFuncionais().remove(unidade);
		if (unidade != null && unidade.getSeq() != null) {
			listaExcluiUnidadeFunc.add(unidade);
		}else {
			listaUnidadeFuncAdicionadas.remove(unidade);
		}
	}

	public boolean isDesabilitarCodigo() {
		return desabilitarCodigo;
	}

	public void setDesabilitarCodigo(boolean desabilitarCodigo) {
		this.desabilitarCodigo = desabilitarCodigo;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public boolean isExigeComplemento() {
		return exigeComplemento;
	}

	public void setExigeComplemento(boolean exigeComplemento) {
		this.exigeComplemento = exigeComplemento;
	}

	public boolean isOutros() {
		return outros;
	}

	public void setOutros(boolean outros) {
		this.outros = outros;
	}

	public boolean isHiddenCodigo() {
		return hiddenCodigo;
	}

	public void setHiddenCodigo(boolean hiddenCodigo) {
		this.hiddenCodigo = hiddenCodigo;
	}

	public Integer getAinTipoDietaCodigo() {
		return ainTipoDietaCodigo;
	}

	public void setAinTipoDietaCodigo(Integer ainTipoDietaCodigo) {
		this.ainTipoDietaCodigo = ainTipoDietaCodigo;
	}

	public AnuTipoItemDieta getTipoDieta() {
		return tipoDieta;
	}

	public void setTipoDieta(AnuTipoItemDieta tipoDieta) {
		this.tipoDieta = tipoDieta;
	}

	public MpmUnidadeMedidaMedica getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(MpmUnidadeMedidaMedica unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	public MpmTipoFrequenciaAprazamento getTipoAprazamento() {
		return tipoAprazamento;
	}

	public void setTipoAprazamento(MpmTipoFrequenciaAprazamento tipoAprazamento) {
		this.tipoAprazamento = tipoAprazamento;
	}

	public List<AghUnidadesFuncionais> getListaUnidadesFuncionais() {
		return listaUnidadesFuncionais;
	}

	public void setListaUnidadesFuncionais(List<AghUnidadesFuncionais> listaUnidadesFuncionais) {
		this.listaUnidadesFuncionais = listaUnidadesFuncionais;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public AghUnidadesFuncionais getUnidadeFuncionalSelecionada() {
		return unidadeFuncionalSelecionada;
	}

	public void setUnidadeFuncionalSelecionada(AghUnidadesFuncionais unidadeFuncionalSelecionada) {
		this.unidadeFuncionalSelecionada = unidadeFuncionalSelecionada;
	}

	public IModeloBasicoFacade getModeloBasicoFacade() {
		return modeloBasicoFacade;
	}

	public void setModeloBasicoFacade(IModeloBasicoFacade modeloBasico) {
		this.modeloBasicoFacade = modeloBasico;
	}

	public List<AnuTipoItemDietaUnfs> getListaExcluiUnidadeFunc() {
		return listaExcluiUnidadeFunc;
	}

	public void setListaExcluiUnidadeFunc(List<AnuTipoItemDietaUnfs> listaExcluiUnidadeFunc) {
		this.listaExcluiUnidadeFunc = listaExcluiUnidadeFunc;
	}

	public List<AnuTipoItemDietaUnfs> getListaUnidadeFuncAdicionadas() {
		return listaUnidadeFuncAdicionadas;
	}

	public void setListaUnidadeFuncAdicionadas(
			List<AnuTipoItemDietaUnfs> listaUnidadeFuncAdicionadas) {
		this.listaUnidadeFuncAdicionadas = listaUnidadeFuncAdicionadas;
	}

	public List<AnuTipoItemDietaUnfs> getTipoDietaUnidadeFuncionais() {
		return tipoDietaUnidadeFuncionais;
	}

	public void setTipoDietaUnidadeFuncionais(
			List<AnuTipoItemDietaUnfs> tipoDietaUnidadeFuncionais) {
		this.tipoDietaUnidadeFuncionais = tipoDietaUnidadeFuncionais;
	}

	private static class UnidadeFuncionalComparator implements Comparator<AghUnidadesFuncionais> {
		@Override
		public int compare(AghUnidadesFuncionais unf1, AghUnidadesFuncionais unf2) {
			String andarAlaDescricaoUnf1 = getLPADAndarAlaDescricao(unf1);
			String andarAlaDescricaoUnf2 = getLPADAndarAlaDescricao(unf2);
			return andarAlaDescricaoUnf1.compareToIgnoreCase(andarAlaDescricaoUnf2);
		}

		public String getLPADAndarAlaDescricao(AghUnidadesFuncionais unf) {
			return (unf.getAndar() != null ? StringUtils.leftPad(unf.getAndar().toString(), 2, "0") : "") + " " + (unf.getIndAla() != null ? unf.getIndAla().toString() : "")
					+ " - " + (unf.getDescricao() != null ? unf.getDescricao() : "");
		}
	}
}
