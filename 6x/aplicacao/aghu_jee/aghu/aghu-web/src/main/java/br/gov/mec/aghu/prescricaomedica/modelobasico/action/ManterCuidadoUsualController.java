package br.gov.mec.aghu.prescricaomedica.modelobasico.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmCuidadoUsualUnf;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.action.TiposDietaCrudController;
import br.gov.mec.aghu.prescricaomedica.modelobasico.business.IModeloBasicoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterCuidadoUsualController extends ActionController {

	private static final long serialVersionUID = -4696668882790099490L;

	private static final String PAGE_PESQUISAR_CUIDADO_USUAL = "pesquisarCuidadoUsual";

	@EJB
	private IModeloBasicoFacade modeloBasicoFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IAghuFacade aghuFacade;

	private MpmCuidadoUsual cuidadoUsual;

	//private Integer cuidadoUsualSeq;

	// private Short unidFuncionalSeq;

	private AghUnidadesFuncionais parametroSelecionado;

	// usado na frequencia
	private Short frequencia;
	private MpmTipoFrequenciaAprazamento tipoAprazamento;

	private List<MpmCuidadoUsualUnf> listaCuidadoUsualUnf;
	private List<AghUnidadesFuncionais> listaUnidadesFuncionais;
	private List<Short> ufsInseridas;
	private List<Short> ufsExcluidas;
	private AghUnidadesFuncionais unidadeFuncional, unidadeFuncionalSelecionada;

	private boolean alterar = false;

	public boolean verificaRequiredFrequencia() {
		return this.tipoAprazamento != null && this.tipoAprazamento.getIndDigitaFrequencia();
	}

	public void verificarFrequencia() {
		if (!this.verificaRequiredFrequencia()) {
			this.frequencia = null;
		}
	}

	public String getDescricaoTipoFrequenciaAprazamento() {
		return buscaDescricaoTipoFrequenciaAprazamento(this.tipoAprazamento);
	}

	public String buscaDescricaoTipoFrequenciaAprazamento(MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		return tipoFrequenciaAprazamento != null ? tipoFrequenciaAprazamento.getDescricaoSintaxeFormatada(this.frequencia) : "";
	}

	public List<MpmTipoFrequenciaAprazamento> buscarTiposFrequenciaAprazamento(String strPesquisa) {
		return this.prescricaoMedicaFacade.buscarTipoFrequenciaAprazamento((String) strPesquisa);
	}

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	public void iniciar() {
	 

		if (this.cuidadoUsual != null) {

			this.setTipoAprazamento(this.cuidadoUsual.getMpmTipoFreqAprazamentos());
			this.preparaAprazamento();

			try {
				listaCuidadoUsualUnf = getModeloBasicoFacade().listaUnidadeFuncionalPorCuidadoUsual(cuidadoUsual.getSeq());

				ufsInseridas = new ArrayList<>();
				ufsExcluidas = new ArrayList<>();
				listaUnidadesFuncionais = new ArrayList<AghUnidadesFuncionais>();
				for (MpmCuidadoUsualUnf unidadeFuncional : listaCuidadoUsualUnf) {
					AghUnidadesFuncionais uniFun = this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(unidadeFuncional.getId().getUnfSeq());
					AghUnidadesFuncionais vo = new AghUnidadesFuncionais();
					vo.setSeq(uniFun.getSeq());
					vo.setDescricao(uniFun.getDescricao());
					vo.setAndar(uniFun.getAndar());
					vo.setIndAla(uniFun.getIndAla());

					listaUnidadesFuncionais.add(vo);
				}

				Collections.sort(listaUnidadesFuncionais, TiposDietaCrudController.UNIDADE_COMPARATOR);

			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}

		} else {
			this.cuidadoUsual = new MpmCuidadoUsual();
			this.cuidadoUsual.setIndSituacao(DominioSituacao.A);
			this.listaCuidadoUsualUnf = new ArrayList<MpmCuidadoUsualUnf>();
			this.listaUnidadesFuncionais = new ArrayList<AghUnidadesFuncionais>();
			this.ufsInseridas = new ArrayList<>();
			this.ufsExcluidas = new ArrayList<>();
		}
	
	}

	public void cadastrar() {
		this.cuidadoUsual = new MpmCuidadoUsual();
		this.listaCuidadoUsualUnf = new ArrayList<MpmCuidadoUsualUnf>();
		this.listaUnidadesFuncionais = new ArrayList<AghUnidadesFuncionais>();
		this.ufsInseridas = new ArrayList<>();
		this.ufsExcluidas = new ArrayList<>();
		this.unidadeFuncional = new AghUnidadesFuncionais();
		this.alterar = false;
	}

	/**
	 * Salva inclusão ou alteração de registro.
	 */
	public String gravar() {
		try {
			this.cuidadoUsual.setFrequencia((this.frequencia != null) ? this.frequencia.shortValue() : null);

			this.validaAprazamento();

			if (isAlterar()) {
				this.modeloBasicoFacade.alterar(cuidadoUsual, ufsInseridas, ufsExcluidas);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_CUIDADO_USUAL", this.cuidadoUsual.getDescricao());
			} else {
				this.modeloBasicoFacade.inserir(this.cuidadoUsual, ufsInseridas);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_CUIDADO_USUAL", this.cuidadoUsual.getDescricao());
			}
			
			limpar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		} catch (BaseRuntimeException ex) {
			apresentarExcecaoNegocio(ex);
			return null;
		}

		return PAGE_PESQUISAR_CUIDADO_USUAL;
	}

	/**
	 * Método para limpar o formulário da tela.
	 */
	public void limpar() {
		this.setCuidadoUsual(null);
		this.setUnidadeFuncional(null);
		this.setListaCuidadoUsualUnf(null);
		this.setListaUnidadesFuncionais(null);
		this.setUfsInseridas(null);
		this.setUfsInseridas(null);
		this.setAlterar(false);
		this.setFrequencia(null);
		this.setTipoAprazamento(null);
		this.setParametroSelecionado(null);
	}

	public String cancelar() {
		this.limpar();
		return PAGE_PESQUISAR_CUIDADO_USUAL;
	}

	public void preparaAprazamento() {
		if (this.cuidadoUsual.getFrequencia() != null) {
			this.frequencia = this.cuidadoUsual.getFrequencia();
		}

	}

	/* ---- INÍCIO DOS CONTROLES DO APRAZAMENTO ---- */

	public void validaAprazamento() {

		if (this.frequencia != null) {
			cuidadoUsual.setFrequencia(this.frequencia.shortValue());
		}

		if (this.getTipoAprazamento() != null) {

			cuidadoUsual.setMpmTipoFreqAprazamentos(this.getTipoAprazamento());

		} else {
			cuidadoUsual.setMpmTipoFreqAprazamentos(null);
			/*
			 * Esta linha foi comentada referente ao bug #6406
			 * apresentarExcecaoNegocio(new ApplicationBusinessException(
			 * ManterCuidadoUsualControllerExceptionCode
			 * .MENSAGEM_TIPO_NAO_EXISTE, "[]"));
			 */
			return;
		}
	}

	// declaração do VO da lista de aprazamentos - muda a sintaxe
	// automaticamente pelo get da descrição
	public class MpmTipoFrequenciaAprazamentoComboBoxVO {

		private MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento;

		public MpmTipoFrequenciaAprazamentoComboBoxVO() {
		}

		public MpmTipoFrequenciaAprazamento getTipoFrequenciaAprazamento() {
			return tipoFrequenciaAprazamento;
		}

		public void setTipoFrequenciaAprazamento(MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
			this.tipoFrequenciaAprazamento = tipoFrequenciaAprazamento;
		}

		public String getDescricao() {
			return (this.tipoFrequenciaAprazamento.getSintaxe() == null ? this.tipoFrequenciaAprazamento.getDescricao() : getFrequenciaFormatada(this.tipoFrequenciaAprazamento
					.getSintaxe())).toUpperCase();
		}

		private String getFrequenciaFormatada(String sintaxe) {
			String returnValue = sintaxe.replaceAll("#", frequencia != null ? frequencia.toString() : "");
			return returnValue;
		}

		public String getSigla() {
			return this.tipoFrequenciaAprazamento != null ? this.tipoFrequenciaAprazamento.getSigla() : null;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result + ((tipoFrequenciaAprazamento == null) ? 0 : tipoFrequenciaAprazamento.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			MpmTipoFrequenciaAprazamentoComboBoxVO other = (MpmTipoFrequenciaAprazamentoComboBoxVO) obj;
			if (!getOuterType().equals(other.getOuterType())) {
				return false;
			}
			if (tipoFrequenciaAprazamento == null) {
				if (other.tipoFrequenciaAprazamento != null) {
					return false;
				}
			} else if (!tipoFrequenciaAprazamento.equals(other.tipoFrequenciaAprazamento)) {
				return false;
			}
			return true;
		}

		private ManterCuidadoUsualController getOuterType() {
			return ManterCuidadoUsualController.this;
		}
	}

	/* ---- FIM DOS CONTROLES DO APRAZAMENTO ---- */

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
			result = new HashSet<AghUnidadesFuncionais>(aghuFacade.pesquisarAghUnidadesFuncionaisAtivasPorSeqOuDescricao(parametro, 0, 100, AghUnidadesFuncionais.Fields.DESCRICAO.toString(), true));
		} else {
			// adiciona a selecionada para nao mostrar mensagens erradas na tela
			result.add(unidadeFuncionalSelecionada);
		}
		List<AghUnidadesFuncionais> resultReturn = new ArrayList<AghUnidadesFuncionais>(result);
		Collections.sort(resultReturn, TiposDietaCrudController.UNIDADE_COMPARATOR);
		return resultReturn;
	}

	/**
	 * Método que exclui uma unidade funcional da lista em memória Ignora nulos
	 * 
	 * @param espParaExcluir
	 */
	public void excluirUnidadeFuncional() {
		ufsExcluidas.add(parametroSelecionado.getSeq());
		this.listaUnidadesFuncionais.remove(this.parametroSelecionado);
	}

	/**
	 * Adiciona uma equipe na lista em memória Caso a especialidade já esteja na
	 * lista, ou seja nula, ignora
	 */
	public void adicionarUnidadeFuncional() {	
		 if (getUnidadeFuncionalSelecionada() == null) {
			 apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_UNIDADE_FUNCIONAL_CUIDADO_OBRIGATORIA");
			 return;
			 
		 } else if(listaUnidadesFuncionais.contains(getUnidadeFuncionalSelecionada())) {
			 apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_UNIDADE_FUNCIONAL_JA_ASSOCIADA");
			 
		 } else {
			listaUnidadesFuncionais.add(getUnidadeFuncionalSelecionada());
			ufsInseridas.add(getUnidadeFuncionalSelecionada().getSeq());
			Collections.sort(listaUnidadesFuncionais, TiposDietaCrudController.UNIDADE_COMPARATOR);
			setUnidadeFuncional(null);
			setUnidadeFuncionalSelecionada(null);
		}
	}

	public void selecionouUnidadeFuncional() {
		setUnidadeFuncionalSelecionada(getUnidadeFuncional());
	}
	
	public void apagarUnidadeFuncional(){
		setUnidadeFuncionalSelecionada(null);
	}

	public IModeloBasicoFacade getModeloBasicoFacade() {
		return modeloBasicoFacade;
	}

	public void setModeloBasicoFacade(IModeloBasicoFacade modeloBasicoFacade) {
		this.modeloBasicoFacade = modeloBasicoFacade;
	}

	public MpmCuidadoUsual getCuidadoUsual() {
		return cuidadoUsual;
	}

	public void setCuidadoUsual(MpmCuidadoUsual cuidadoUsual) {
		this.cuidadoUsual = cuidadoUsual;
	}

	public boolean isAlterar() {
		return alterar;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
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

	public Short getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Short freq) {
		this.frequencia = freq;
	}

	public List<AghUnidadesFuncionais> getListaUnidadesFuncionais() {
		return listaUnidadesFuncionais;
	}

	public void setListaUnidadesFuncionais(List<AghUnidadesFuncionais> listaUnidadesFuncionais) {
		this.listaUnidadesFuncionais = listaUnidadesFuncionais;
	}

	public List<MpmCuidadoUsualUnf> getListaCuidadoUsualUnf() {
		return listaCuidadoUsualUnf;
	}

	public void setListaCuidadoUsualUnf(List<MpmCuidadoUsualUnf> listaCuidadoUsualUnf) {
		this.listaCuidadoUsualUnf = listaCuidadoUsualUnf;
	}

	public void setTipoAprazamento(MpmTipoFrequenciaAprazamento tipoAprazamento) {
		this.tipoAprazamento = tipoAprazamento;
	}

	public MpmTipoFrequenciaAprazamento getTipoAprazamento() {
		return tipoAprazamento;
	}

	public AghUnidadesFuncionais getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(AghUnidadesFuncionais parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

	public List<Short> getUfsInseridas() {
		return ufsInseridas;
	}

	public void setUfsInseridas(List<Short> ufsInseridas) {
		this.ufsInseridas = ufsInseridas;
	}

	public List<Short> getUfsExcluidas() {
		return ufsExcluidas;
	}

	public void setUfsExcluidas(List<Short> ufsExcluidas) {
		this.ufsExcluidas = ufsExcluidas;
	}

}
