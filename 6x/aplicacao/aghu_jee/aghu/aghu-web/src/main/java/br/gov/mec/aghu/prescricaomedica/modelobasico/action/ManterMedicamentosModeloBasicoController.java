package br.gov.mec.aghu.prescricaomedica.modelobasico.action;

import java.math.BigDecimal;
import java.text.Collator;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioUnidadeHorasMinutos;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.internacao.vo.MedicamentoVO;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.model.AfaViaAdministracao;
import br.gov.mec.aghu.model.MpmItemModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamento;
import br.gov.mec.aghu.model.MpmModeloBasicoMedicamentoId;
import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.prescricaomedica.action.ManterPrescricaoMedicamentoExceptionCode;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.modelobasico.business.IModeloBasicoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.view.VAfaDescrMdto;
import br.gov.mec.aghu.view.VMpmDosagem;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@SuppressWarnings("PMD.AghuTooManyMethods")
public class ManterMedicamentosModeloBasicoController extends ActionController {

	private static final Log LOG = LogFactory.getLog(ManterMedicamentosModeloBasicoController.class);
	private static final long serialVersionUID = 7952176099581400318L;

	private static final String PAGE_PRESCRICAO_MEDICA_MANTER_ITENS_MODELO_BASICO = "prescricaomedica-manterItensModeloBasico";

	@EJB
	private IModeloBasicoFacade modeloBasicoFacade;

	@EJB
	private IFarmaciaFacade farmaciaFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	// chave do moviadelo de medicamento recebida via parametro
	private Integer modeloBasicoPrescricaoSeq;
	private Integer seq;

	private MpmModeloBasicoPrescricao modeloBasico;
	private MpmModeloBasicoMedicamento modeloBasicoMedicamento;
	private MpmItemModeloBasicoMedicamento itemMedicamento;

	private MedicamentoVO medicamentoVO;// Usado na suggestionBox de
										// medicamentos.
	List<VMpmDosagem> listaDosagens = new ArrayList<VMpmDosagem>();
	private VMpmDosagem unidadeDosagem;

	private boolean altera;
	private Date horaInicioAdministracao;

	private List<MpmModeloBasicoMedicamento> listaMedicamentos = new ArrayList<MpmModeloBasicoMedicamento>();

	private boolean todasAsVias;
	private boolean exibirModal;
	private String mensagemExibicaoModal;

	private Integer modeloBasicoPrescricaoSeqExcluir;
	private Integer seqExcluir;

	// usado na frequencia
	private Short frequencia;
	private MpmTipoFrequenciaAprazamento tipoAprazamento;

	private VAfaDescrMdto diluente;
	private BigDecimal volumeDiluenteMl;
	private DominioUnidadeHorasMinutos unidHorasCorrer;
	private Boolean indBombaInfusao;

	// Campos para o controle de alteração no formulário do item
	private Integer seqEdicao;

	private enum ManterMedicamentosModeloBasicoControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_MODELO_NAO_INFORMADO, MENSAGEM_ITEM_NAO_INFORMADO, MENSAGEM_ITEM_NAO_ENCONTRADO, MENSAGEM_ALTERACOES_PENDENTES, MENSAGEM_TIPO_NAO_EXISTE, MSG_MODAL_CONFIRMACAO_VIA, DOSE_PRECISA_SER_MAIOR_QUE_ZERO, HORA_INVALIDA, MENSAGEM_VIA_NAO_PERMITE_BI;
	}

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
	 

		try {
			if (modeloBasicoPrescricaoSeq == null) {
				throw new ApplicationBusinessException(ManterMedicamentosModeloBasicoControllerExceptionCode.MENSAGEM_MODELO_NAO_INFORMADO);
			}
			this.modeloBasico = this.modeloBasicoFacade.obterModeloBasico(modeloBasicoPrescricaoSeq);
			this.obterListaMedicamentos();
			// verifica se a inclusao ou alteracao
			if (seq == null) {
				limpar();
			} else {
				this.preparaAlterar(this.modeloBasicoPrescricaoSeq, seq);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e); // trata erro de parametro de sistema
		}
	
	}

	public void novoItem() {
		this.modeloBasicoMedicamento = new MpmModeloBasicoMedicamento();
		try {
			this.modeloBasicoMedicamento.setServidor(registroColaboradorFacade.obterServidorPorUsuario(obterLoginUsuarioLogado()));
			this.modeloBasicoMedicamento.setModeloBasicoPrescricao(modeloBasico);
			this.itemMedicamento = new MpmItemModeloBasicoMedicamento();
			this.itemMedicamento.setModeloBasicoMedicamento(modeloBasicoMedicamento);
			this.itemMedicamento.setServidor(registroColaboradorFacade.obterServidorPorUsuario(obterLoginUsuarioLogado()));
			this.itemMedicamento.setDose(null);
		} catch (ApplicationBusinessException e) {
			LOG.equals("Não encontrou servidor logado!");
		} // logado!

	}

	public void limpar() {
		this.exibirModal = false;
		this.altera = false;

		this.frequencia = null;
		this.tipoAprazamento = null;
		this.horaInicioAdministracao = null;
		this.unidadeDosagem = null;
		this.medicamentoVO = null;
		this.todasAsVias = false;
		this.seq = null;
		this.diluente = null;
		this.volumeDiluenteMl = null;
		this.unidHorasCorrer = null;
		this.indBombaInfusao = false;
		this.listaDosagens = new ArrayList<VMpmDosagem>();
		novoItem();
		obterListaMedicamentos();
	}
	
	@SuppressWarnings("PMD.NPathComplexity")
	public void gravar() {
		try {
			if (indBombaInfusao != null && indBombaInfusao && !modeloBasicoMedicamento.getViaAdministracao().getIndPermiteBi()) {
				throw new ApplicationBusinessException(ManterMedicamentosModeloBasicoControllerExceptionCode.MENSAGEM_VIA_NAO_PERMITE_BI);
			}
			if (this.getModeloBasicoMedicamento().getQuantidadeHorasCorrer() != null && this.unidHorasCorrer == null) {
				throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.OBRIGATORIO_PREENCHER_UNIDADE_AO_PREENCHER_CORRER_EM);
			}
			if (this.getModeloBasicoMedicamento().getQuantidadeHorasCorrer() == null && this.unidHorasCorrer != null) {
				throw new ApplicationBusinessException(ManterPrescricaoMedicamentoExceptionCode.OBRIGATORIO_PREENCHER_CORRER_EM_AO_PREENCHER_UNIDADE);
			}
			if (this.tipoAprazamento == null) {
				apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Tipo de Aprazamento");
			} else if (this.verificaRequiredFrequencia() && this.frequencia == null) {
				apresentarMsgNegocio(Severity.ERROR, "CAMPO_OBRIGATORIO", "Frequência");
			} else {
				this.modeloBasicoMedicamento.setFrequencia(this.frequencia);
				if (this.tipoAprazamento != null) {
					this.modeloBasicoMedicamento.setTipoFrequenciaAprazamento(this.tipoAprazamento);
				}
				modeloBasicoMedicamento.setDiluente(this.diluente != null ? this.diluente.getMedicamento() : null);
				modeloBasicoMedicamento.setVolumeDiluenteMl(this.volumeDiluenteMl);
				modeloBasicoMedicamento.setUnidHorasCorrer(this.unidHorasCorrer);
				modeloBasicoMedicamento.setIndBombaInfusao(this.indBombaInfusao);

				if (unidadeDosagem != null) {
					this.itemMedicamento.setFormaDosagem(unidadeDosagem.getFormaDosagem());
				}
				if (horaInicioAdministracao != null) {
					modeloBasicoMedicamento.setHoraInicioAdministracao(this.horaInicioAdministracao);
				}
				itemMedicamento.setModeloBasicoMedicamento(this.modeloBasicoMedicamento);
				if (!this.altera) {
					this.modeloBasicoFacade.inserir(itemMedicamento);
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_MEDICAMENTO_MODELO", itemMedicamento.getMedicamento().getDescricao());
				} else {
					this.modeloBasicoFacade.alterar(itemMedicamento);
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_MEDICAMENTO_MODELO", itemMedicamento.getMedicamento().getDescricao());
				}
				this.limpar();
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e); // trata erro de parametro de sistema
		}
	}

	/**
	 * Retorna lista de medicamentos de determinado modelo
	 */
	public void obterListaMedicamentos() {
		this.listaMedicamentos = this.modeloBasicoFacade.obterListaMedicamentosModelo(this.modeloBasicoPrescricaoSeq);
		this.ordenaLista(this.listaMedicamentos);
	}

	/**
	 * ordena lista de medicamentos alfabeticamente, considerando acentos na
	 * comparacao
	 * 
	 * @param listaMedicamentos
	 */
	private void ordenaLista(List<MpmModeloBasicoMedicamento> listaMedicamentos) {
		final Collator collator = Collator.getInstance(new Locale("pt", "BR"));
		collator.setStrength(Collator.PRIMARY);
		Collections.sort(listaMedicamentos, new Comparator<MpmModeloBasicoMedicamento>() {
			public int compare(MpmModeloBasicoMedicamento mpmModeloBasicoMedicamento, MpmModeloBasicoMedicamento mpmModeloBasicoMedicamento1) {
				return collator.compare(getDescricaoEditada(mpmModeloBasicoMedicamento.getId().getModeloBasicoPrescricaoSeq(), mpmModeloBasicoMedicamento.getId().getSeq()),
						getDescricaoEditada(mpmModeloBasicoMedicamento1.getId().getModeloBasicoPrescricaoSeq(), mpmModeloBasicoMedicamento1.getId().getSeq()));
			}
		});
	}

	public String getDescricaoEditada(Integer modBasicoPrescricaoSeq, Integer modSeq) {
		MpmModeloBasicoMedicamento modelo = this.modeloBasicoFacade.obterModeloBasicoMedicamento(modBasicoPrescricaoSeq, modSeq);
		if (modelo != null) {
			return this.modeloBasicoFacade.getDescricaoEditadaMedicamentoItem(modelo);
		}
		return null;
	}

	public void preparaAlterar(Integer modeloBasicoPrescricaoSeq, Integer seq) {

		this.modeloBasicoPrescricaoSeq = modeloBasicoPrescricaoSeq;
		this.seqEdicao = seq;
		this.editarItem();
	}
	
	@SuppressWarnings("PMD.NPathComplexity")
	public void editarItem() {

		Integer modeloBasicoPrescricaoSeq = this.modeloBasicoPrescricaoSeq;
		Integer seq = this.seqEdicao;

		try {
			this.limpar();

			this.seq = seq;
			// BUSCA MODELO
			this.modeloBasicoMedicamento = new MpmModeloBasicoMedicamento();
			this.modeloBasicoMedicamento = this.modeloBasicoFacade.obterModeloBasicoMedicamento(new MpmModeloBasicoMedicamentoId(this.modeloBasicoPrescricaoSeq, this.seq), true,
					MpmModeloBasicoMedicamento.Fields.TIPO_FREQ_APRAZAMENTO, MpmModeloBasicoMedicamento.Fields.TIPO_VELOCIDADE_ADMINISTRACAO,MpmModeloBasicoMedicamento.Fields.VIA_ADMINISTRACAO);
			// BUSCA ITEM - medicamento sempre tera apenas 1
			List<MpmItemModeloBasicoMedicamento> lista;
			lista = this.modeloBasicoFacade.obterItemMedicamento(modeloBasicoPrescricaoSeq, seq);
			this.itemMedicamento = new MpmItemModeloBasicoMedicamento();
			this.itemMedicamento = !lista.isEmpty() ? lista.get(0) : null;
			// BUSCA MEDICAMENTO
			if (this.itemMedicamento != null && this.itemMedicamento.getMedicamento() != null) {
				this.medicamentoVO = new MedicamentoVO();
				this.medicamentoVO.setMatCodigo(this.itemMedicamento.getMedicamento().getMatCodigo());
				this.medicamentoVO.setDescricaoEditada(this.itemMedicamento.getMedicamento().getDescricaoEditada());
				this.medicamentoVO.setMedicamento(this.itemMedicamento.getMedicamento());
			}

			// se não encontrou modelo, item ou medicamento=erro!
			if (this.modeloBasicoMedicamento == null || this.itemMedicamento == null || medicamentoVO == null) {
				throw new ApplicationBusinessException(ManterMedicamentosModeloBasicoControllerExceptionCode.MENSAGEM_ITEM_NAO_ENCONTRADO);
			}

			// INICIA O HORÁRIO
			if (modeloBasicoMedicamento.getHoraInicioAdministracao() != null) {
				this.horaInicioAdministracao = modeloBasicoMedicamento.getHoraInicioAdministracao();
			}

			// INICIA frequencia
			this.frequencia = this.modeloBasicoMedicamento.getFrequencia();

			// INICIA APRAZAMENTO
			this.tipoAprazamento = this.modeloBasicoMedicamento.getTipoFrequenciaAprazamento();

			// INICIA UNIDADE DOSAGEM
			if (this.itemMedicamento.getFormaDosagem() != null) {
				this.atualizaListaDosagens(this.itemMedicamento.getMedicamento().getMatCodigo());
				for (VMpmDosagem dosagem : this.listaDosagens) {
					if (dosagem.getFormaDosagem().getSeq().equals(this.itemMedicamento.getFormaDosagem().getSeq())) {
						unidadeDosagem = dosagem;
					}
				}
			} else {
				unidadeDosagem = null;
			}

			this.diluente = (modeloBasicoMedicamento.getDiluente() != null) ? this.farmaciaFacade.obtemListaDiluentes(modeloBasicoMedicamento.getDiluente().getMatCodigo()).get(0) : null;
			this.volumeDiluenteMl = modeloBasicoMedicamento.getVolumeDiluenteMl();
			this.unidHorasCorrer = modeloBasicoMedicamento.getUnidHorasCorrer();
			this.indBombaInfusao = modeloBasicoMedicamento.getIndBombaInfusao();

			this.altera = true;

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e); // trata erro de parametro de sistema
		}
	}

	public void excluir() {
		try {
			if (this.modeloBasicoPrescricaoSeqExcluir != null && this.seqExcluir != null) {
				MpmModeloBasicoMedicamento medicamentoExcluir = this.modeloBasicoFacade.obterModeloBasicoMedicamento(this.modeloBasicoPrescricaoSeqExcluir, this.seqExcluir);
				if (medicamentoExcluir != null) {
					String desc = this.modeloBasicoFacade.getDescricaoEditadaMedicamento(medicamentoExcluir);
					this.modeloBasicoFacade.excluir(medicamentoExcluir);
					this.obterListaMedicamentos();
					this.limpar();
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_MEDICAMENTO_MODELO", desc);
				} else {
					throw new ApplicationBusinessException(ManterMedicamentosModeloBasicoControllerExceptionCode.MENSAGEM_MODELO_NAO_INFORMADO);
				}

			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e); // trata erro de parametro de sistema
		}
	}

	public void cancelarModal() {
		this.modeloBasicoPrescricaoSeqExcluir = null;
		this.seqExcluir = null;
		this.seqEdicao = null;
	}

	public String voltar() {
		limpar();
		return PAGE_PRESCRICAO_MEDICA_MANTER_ITENS_MODELO_BASICO;
	}

	/*--
	 *    LISTA DE MEDICAMENTOS
	 */
	public List<MedicamentoVO> obterMedicamentosVO(String strPesquisa) {
		// Lista de Medicamentos Ativos, Padronizados, Que não exigem Observação
		// e não são Antimicrobianos
		return this.returnSGWithCount(this.farmaciaFacade.obterMedicamentosModeloBasicoVO((String) strPesquisa),obterMedicamentosVOCount(strPesquisa));
	}

	public Long obterMedicamentosVOCount(String strPesquisa) {
		return this.farmaciaFacade.obterMedicamentosModeloBasicoVOCount((String) strPesquisa);
	}

	public void realizarVerificacoesMedicamento() {

		// arruma a descrição do medicamento que será mostrada pela sugestion
		this.formataDescricaoMedicamento();

		if (medicamentoVO != null) {
			itemMedicamento.setMedicamento(this.farmaciaFacade.obterMedicamento(this.medicamentoVO.getMatCodigo()));
			atualizaListaDosagens(this.itemMedicamento.getMedicamento().getMatCodigo());
		} else {
			itemMedicamento.setMedicamento(null);
			// limparCamposRelacionados();
		}

		// conta numero de vias ,
		// se numero de vias = 0
		// então Marcar checkbox "Todas as vias"
		Long count = 0L;
		if (this.itemMedicamento.getMedicamento() != null) {
			List<Integer> listaDeIds = new ArrayList<Integer>();
			listaDeIds.add(this.itemMedicamento.getMedicamento().getMatCodigo());
			count = this.farmaciaFacade.listarViasMedicamentoCount(null, listaDeIds, null);
		}
		if (count == 0) {
			this.todasAsVias = true;
		} else {
			this.todasAsVias = false;
		}
	}

	public void formataDescricaoMedicamento() {

		this.medicamentoVO.setMedicamento(this.farmaciaFacade.obterMedicamento(this.medicamentoVO.getMatCodigo()));

		StringBuilder returnValue = new StringBuilder("");
		if (medicamentoVO.getDescricaoMat() != null) {
			returnValue.append(medicamentoVO.getDescricaoMedicamento());
			returnValue.append(' ');
		}
		if (medicamentoVO.getConcentracao() != null) {
			Locale locBR = new Locale("pt", "BR");// Brasil
			DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
			dfSymbols.setDecimalSeparator(',');
			DecimalFormat format;
			if (this.medicamentoVO.getConcentracao() != null && this.medicamentoVO.getConcentracao().stripTrailingZeros().scale() <= 0) {
				format = new DecimalFormat("#,###,###,###,##0.###############", dfSymbols);
				returnValue.append(format.format(this.medicamentoVO.getConcentracao()));
				returnValue.append(' ');
			} else if (this.medicamentoVO.getConcentracao() != null) {
				format = new DecimalFormat("#,###,###,###,##0.0##############", dfSymbols);
				returnValue.append(format.format(this.medicamentoVO.getConcentracao()));
				returnValue.append(' ');
			}
		}
		if (medicamentoVO.getDescricaoUnidadeMedica() != null) {
			returnValue.append(this.medicamentoVO.getDescricaoUnidadeMedica());
		}
		medicamentoVO.setDescricaoEditada(returnValue.toString());
	}

	public void atualizaListaDosagens(Integer medMatCodigo) {
		this.listaDosagens = new ArrayList<VMpmDosagem>();
		if (medMatCodigo != null) {
			this.listaDosagens = this.prescricaoMedicaFacade.buscarDosagensMedicamento(medMatCodigo);
		}
		if (!listaDosagens.isEmpty()) {
			if (listaDosagens.size() == 1) {
				// se so existe UMA formaDosagem possivel para o
				// medicamento,
				// esta deve vir selecionada
				unidadeDosagem = listaDosagens.get(0);
			} else {
				// se existe formaDosagem padrão, deve vir selecionada
				AfaFormaDosagem formaDosagem = this.farmaciaFacade.buscarDosagenPadraoMedicamento(medMatCodigo);
				if (formaDosagem != null) {
					for (int i = 0; i < listaDosagens.size(); i++) {
						if (formaDosagem.getSeq().equals(listaDosagens.get(i).getFormaDosagem().getSeq())) {
							this.unidadeDosagem = listaDosagens.get(i);
						}
					}
				}
			}
		}
	}

	public List<SelectItem> getSelectListaDosagens() {
		List<SelectItem> lista = new LinkedList<SelectItem>();
		if (this.itemMedicamento.getMedicamento() != null) {
			List<VMpmDosagem> dosagens = this.prescricaoMedicaFacade.buscarDosagensMedicamento(this.itemMedicamento.getMedicamento().getMatCodigo());
			for (VMpmDosagem dosagem : dosagens) {
				// lista.add(new SelectItem(dosagem,
				// dosagem.getDescricaoUnidade()));
				lista.add(new SelectItem(dosagem, dosagem.getSiglaUnidadeMedidaMedica()));

			}
			return lista;
		} else {
			return new LinkedList<SelectItem>();
		}
	}

	public void limparCamposRelacionados() {
		this.itemMedicamento.setFormaDosagem(null);
		this.unidadeDosagem = null;
		this.listaDosagens = new ArrayList<VMpmDosagem>();
		this.todasAsVias = false;
		// modeloBasicoMedicamento.setViaAdministracao(null);
	}

	public boolean permiteDoseFracionada() {
		return this.itemMedicamento.getMedicamento() == null || this.itemMedicamento.getMedicamento().getIndPermiteDoseFracionada();
	}

	public void verificarDose() {
		if (this.itemMedicamento.getDose() != null && this.itemMedicamento.getDose().doubleValue() <= 0) {
			apresentarMsgNegocio(Severity.ERROR, ManterMedicamentosModeloBasicoControllerExceptionCode.DOSE_PRECISA_SER_MAIOR_QUE_ZERO.toString());
		}
	}

	/*
	 * INICIO DA VERIFICAÇÃO DA VIA
	 */
	public List<AfaViaAdministracao> listarViasMedicamento(String strPesquisa) {
		String sigla = (String) strPesquisa;
		//List<AfaViaAdministracao> lista = new ArrayList<AfaViaAdministracao>();
		if (this.todasAsVias) {
			//lista = this.farmaciaFacade.listarTodasAsVias(sigla.toUpperCase(), null);
			return  returnSGWithCount(this.farmaciaFacade.listarTodasAsVias(sigla.toUpperCase(), null), listarViasMedicamentoCount(strPesquisa));
		} else {
			if (this.itemMedicamento.getMedicamento() != null) {
				List<Integer> medMatCodigoList = new ArrayList<Integer>();
				medMatCodigoList.add(this.itemMedicamento.getMedicamento().getMatCodigo());
				//lista = ;
				return  returnSGWithCount(this.farmaciaFacade.listarViasMedicamento(sigla.toUpperCase(), medMatCodigoList, null), listarViasMedicamentoCount(strPesquisa));
			}
		}
		return new ArrayList<AfaViaAdministracao>();
	}

	public Long listarViasMedicamentoCount(String strPesquisa) {
		String sigla = (String) strPesquisa;
		Long count = 0L;
		if (this.todasAsVias) {
			count = this.farmaciaFacade.listarTodasAsViasCount(sigla.toUpperCase(), null);
		} else {
			if (this.itemMedicamento.getMedicamento() != null) {
				List<Integer> listaDeIds = new ArrayList<Integer>();
				listaDeIds.add(this.itemMedicamento.getMedicamento().getMatCodigo());
				count = this.farmaciaFacade.listarViasMedicamentoCount(sigla.toUpperCase(), listaDeIds, null);
			}
		}
		return count;
	}

	public void verificarViaAssociadaAoMedicamento() {

		this.exibirModal = false;
		Boolean viaAssociada = true;
		if (this.todasAsVias && this.medicamentoVO != null) {
			viaAssociada = this.farmaciaFacade.verificarViaAssociadaAoMedicamento(this.medicamentoVO.getMatCodigo(), this.modeloBasicoMedicamento.getViaAdministracao().getSigla());
			if (!viaAssociada) {// Se nao esta associada.
				this.exibirModal = true;
				String msg = WebUtil.initLocalizedMessage(ManterMedicamentosModeloBasicoControllerExceptionCode.MSG_MODAL_CONFIRMACAO_VIA.toString(), null);
				this.mensagemExibicaoModal = MessageFormat.format(msg, this.medicamentoVO.getDescricaoEditada());
				openDialog("modalConfirmacaoViaWG");
			}
		}
	}

	public void desabilitarExibicaoModal() {
		this.exibirModal = false;
	}

	/*
	 * FIM DA VERIFICAÇÃO DA VIA
	 */

	public List<AfaTipoVelocAdministracoes> buscarTiposVelocidadeAdministracao() {
		List<AfaTipoVelocAdministracoes> result = new ArrayList<AfaTipoVelocAdministracoes>();
		result = this.farmaciaFacade.obtemListaTiposVelocidadeAdministracao();
		return result;
	}

	public List<MpmTipoFrequenciaAprazamento> buscarTiposFrequenciaAprazamento(String strPesquisa) {
		return this.returnSGWithCount(this.modeloBasicoFacade.obterListaTipoFrequenciaAprazamento(strPesquisa),this.modeloBasicoFacade.obterListaTipoFrequenciaAprazamentoCount(strPesquisa));
	}

	public String getDescricaoTipoFrequenciaAprazamento() {
		return buscaDescricaoTipoFrequenciaAprazamento(this.tipoAprazamento);
	}

	public String buscaDescricaoTipoFrequenciaAprazamento(MpmTipoFrequenciaAprazamento tipoFrequenciaAprazamento) {
		return tipoFrequenciaAprazamento != null ? tipoFrequenciaAprazamento.getDescricaoSintaxeFormatada(this.frequencia) : "";
	}

	public boolean verificaRequiredFrequencia() {
		return this.tipoAprazamento != null && this.tipoAprazamento.getIndDigitaFrequencia();
	}

	public void verificarFrequencia() {
		if (!this.verificaRequiredFrequencia()) {
			this.frequencia = null;
		}
	}

	public List<VAfaDescrMdto> buscarDiluentes() {
		return this.farmaciaFacade.obtemListaDiluentes();
	}

	// GETTERS AND SETTERS
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

	public MpmModeloBasicoMedicamento getModeloBasicoMedicamento() {
		return modeloBasicoMedicamento;
	}

	public void setModeloBasicoMedicamento(MpmModeloBasicoMedicamento modeloBasicoMedicamento) {
		this.modeloBasicoMedicamento = modeloBasicoMedicamento;
	}

	public MpmItemModeloBasicoMedicamento getItemMedicamento() {
		return itemMedicamento;
	}

	public void setItemMedicamento(MpmItemModeloBasicoMedicamento itemMedicamento) {
		this.itemMedicamento = itemMedicamento;
	}

	public boolean isAltera() {
		return altera;
	}

	public void setAltera(boolean altera) {
		this.altera = altera;
	}

	public List<MpmModeloBasicoMedicamento> getListaMedicamentos() {
		return listaMedicamentos;
	}

	public void setListaMedicamentos(List<MpmModeloBasicoMedicamento> listaMedicamentos) {
		this.listaMedicamentos = listaMedicamentos;
	}

	public Short getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}

	public MedicamentoVO getMedicamentoVO() {
		return medicamentoVO;
	}

	public void setMedicamentoVO(MedicamentoVO medicamentoVO) {
		this.medicamentoVO = medicamentoVO;
	}

	public boolean isTodasAsVias() {
		return todasAsVias;
	}

	public void setTodasAsVias(boolean todasAsVias) {
		this.todasAsVias = todasAsVias;
	}

	public boolean isExibirModal() {
		return exibirModal;
	}

	public void setExibirModal(boolean exibirModal) {
		this.exibirModal = exibirModal;
	}

	public String getMensagemExibicaoModal() {
		return mensagemExibicaoModal;
	}

	public void setMensagemExibicaoModal(String mensagemExibicaoModal) {
		this.mensagemExibicaoModal = mensagemExibicaoModal;
	}

	public List<VMpmDosagem> getListaDosagens() {
		return listaDosagens;
	}

	public void setListaDosagens(List<VMpmDosagem> listaDosagens) {
		this.listaDosagens = listaDosagens;
	}

	public VMpmDosagem getUnidadeDosagem() {
		return unidadeDosagem;
	}

	public void setUnidadeDosagem(VMpmDosagem unidadeDosagem) {
		this.unidadeDosagem = unidadeDosagem;
	}

	public Date getHoraInicioAdministracao() {
		return horaInicioAdministracao;
	}

	public void setHoraInicioAdministracao(Date horaInicioAdministracao) {
		this.horaInicioAdministracao = horaInicioAdministracao;
	}

	public Integer getModeloBasicoPrescricaoSeqExcluir() {
		return modeloBasicoPrescricaoSeqExcluir;
	}
	
	public void setModeloBasicoPrescricaoSeqExcluir(Integer modeloBasicoPrescricaoSeqExcluir) {
		this.modeloBasicoPrescricaoSeqExcluir = modeloBasicoPrescricaoSeqExcluir;
	}

	public void setModeloBasicoFacade(IModeloBasicoFacade modeloBasicoFacade) {
		this.modeloBasicoFacade = modeloBasicoFacade;
	}

	public Integer getSeqExcluir() {
		return seqExcluir;
	}

	public void setSeqExcluir(Integer seqExcluir) {
		this.seqExcluir = seqExcluir;
	}

	public MpmTipoFrequenciaAprazamento getTipoAprazamento() {
		return tipoAprazamento;
	}

	public void setTipoAprazamento(MpmTipoFrequenciaAprazamento tipoAprazamento) {
		this.tipoAprazamento = tipoAprazamento;
	}

	public Boolean getIndBombaInfusao() {
		return indBombaInfusao;
	}

	public VAfaDescrMdto getDiluente() {
		return diluente;
	}

	public void setDiluente(VAfaDescrMdto diluente) {
		this.diluente = diluente;
	}

	public BigDecimal getVolumeDiluenteMl() {
		return volumeDiluenteMl;
	}

	public void setVolumeDiluenteMl(BigDecimal volumeDiluenteMl) {
		this.volumeDiluenteMl = volumeDiluenteMl;
	}

	public DominioUnidadeHorasMinutos getUnidHorasCorrer() {
		return unidHorasCorrer;
	}

	public void setUnidHorasCorrer(DominioUnidadeHorasMinutos unidHorasCorrer) {
		this.unidHorasCorrer = unidHorasCorrer;
	}

	public void setIndBombaInfusao(Boolean indBombaInfusao) {
		this.indBombaInfusao = indBombaInfusao;
	}
}