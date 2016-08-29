package br.gov.mec.aghu.prescricaomedica.modelobasico.action;

import java.math.BigDecimal;
import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoEspecial;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.MpmModeloBasicoModoUsoProcedimento;
import br.gov.mec.aghu.model.MpmModeloBasicoModoUsoProcedimentoId;
import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.model.MpmModeloBasicoProcedimento;
import br.gov.mec.aghu.model.MpmModeloBasicoProcedimentoId;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.MpmTipoModoUsoProcedimento;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.modelobasico.business.IModeloBasicoFacade;
import br.gov.mec.aghu.prescricaomedica.vo.ProcedimentoEspecialVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

@SuppressWarnings("PMD.AghuTooManyMethods")
public class ManterProcedimentosModeloBasicoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4142322231497842265L;

	private static final String PAGE_PRESCRICAO_MEDICA_MANTER_ITENS_MODELO_BASICO = "prescricaomedica-manterItensModeloBasico";

	@EJB
	private IModeloBasicoFacade modeloBasicoFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

    @EJB
    private IParametroFacade parametroFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private Integer seqModelo; // Modelo basico de prescricao
	private Integer seqItemModelo; // Item do modelo basico de

	private MpmModeloBasicoPrescricao modeloBasicoPrescricao;
	private MpmModeloBasicoProcedimentoId modeloBasicoProcedimentoId = new MpmModeloBasicoProcedimentoId();
	private MpmModeloBasicoProcedimento modeloBasicoProcedimento = new MpmModeloBasicoProcedimento();
	private Long procSeq;

	private Boolean ehTipoEspecialDiverso;
	private Boolean ehTipoProcedimentosLeito;
	private Boolean ehTipoOrtesesProteses;

	private Boolean altera = false;
	private Boolean alteraModoUso;

	private boolean confirmaVoltar = false;

	private boolean campoAlteradoFormularioItem;
	private MpmModeloBasicoProcedimento procedimentoEdicao;

	private ProcedimentoEspecialVO procedimento = new ProcedimentoEspecialVO();
	private MpmModeloBasicoModoUsoProcedimento modoUsoProc = new MpmModeloBasicoModoUsoProcedimento();

	private List<MpmModeloBasicoModoUsoProcedimento> listaModoUsoProdedimentoEspecial = new LinkedList<MpmModeloBasicoModoUsoProcedimento>();
	private List<MpmModeloBasicoModoUsoProcedimento> listaModoUsoParaExclusao = new LinkedList<MpmModeloBasicoModoUsoProcedimento>();

	private List<MpmModeloBasicoProcedimento> listaProcedimentoEspecial;

	private Map<MpmModeloBasicoProcedimento, Boolean> procedimentosSelecionados = new HashMap<MpmModeloBasicoProcedimento, Boolean>();

    private String tipoSelecionado;

	private enum ManterProcedimentosModeloBasicoControllerExceptionCode implements BusinessExceptionCode {
		MENSAGEM_MODELO_NAO_INFORMADO, MENSAGEM_MODELO_NAO_EXISTE, MENSAGEM_MODELO_PROCEDIMENTO_NAO_INFORMADO, MENSAGEM_MODO_DE_USO_NAO_PREENCHIDO, MENSAGEM_PROCEDIMENTO_NAO_PREENCHIDO, MENSAGEM_QUANTIDADE_NAO_PREENCHIDA, MENSAGEM_SUCESSO_INCLUSAO_PROCEDIMENTO_MODELO_BASICO, MENSAGEM_MODO_USO_EXIGE_QUANTIDADE, MENSAGEM_QUANTIDADE_MAIOR_ZERO, MENSAGEM_SUCESSO_REMOCAO_MODELO_PROCEDIMENTOS, MENSAGEM_SUCESSO_REMOCAO_MODELO_PROCEDIMENTO, MENSAGEM_NENHUM_MODELO_PROCEDIMENTO_SELECIONADO, MENSAGEM_MODO_USO_EXIGE_QUANTIDADE_MAIOR_ZERO, MENSAGEM_MODO_DE_USO_JA_EXISTE;
	}

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
	 

		
//		if (Boolean.FALSE.equals(getIgnoreInitPageConfig())){
//			return;
//		} else{
//			//setIgnoreInitPageConfig(true);
//		}
		
		try {

			if (seqModelo == null) {
				throw new ApplicationBusinessException(ManterProcedimentosModeloBasicoControllerExceptionCode.MENSAGEM_MODELO_NAO_INFORMADO);
			}

			this.modeloBasicoPrescricao = this.modeloBasicoFacade.obterModeloBasico(seqModelo);

			if (this.modeloBasicoPrescricao == null) {
				throw new ApplicationBusinessException(ManterProcedimentosModeloBasicoControllerExceptionCode.MENSAGEM_MODELO_NAO_EXISTE);
			}

			this.procedimento.setTipo(DominioTipoProcedimentoEspecial.ESPECIAIS_DIVERSOS);
			this.doTipoProcedimentoEspecial();

			// Busca dos procedimentos deste modelo
			this.listaProcedimentoEspecial = this.obterListaProcedimentos(this.modeloBasicoPrescricao);

			// Copia da lista de procedimentos para o mapa utilizado na exclusao
			for (MpmModeloBasicoProcedimento mpmModeloBasicoProcedimento : this.listaProcedimentoEspecial) {
				this.procedimentosSelecionados.put(mpmModeloBasicoProcedimento, false);
			}

			if (this.seqItemModelo != null) {
				// Edicao de um modelo
				this.modeloBasicoProcedimento = this.obterModeloBasicoProcedimento(seqModelo, seqItemModelo);
				this.preparaAlterar(this.modeloBasicoProcedimento);
				this.altera = true;
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e); // trata erro de parâmetro de sistema
		}
	
	}

	private MpmModeloBasicoProcedimento obterModeloBasicoProcedimento(Integer seqModelo, Integer seqItemModelo) {
		return this.modeloBasicoFacade.obterModeloBasicoProcedimento(seqModelo, seqItemModelo);
	}

	private List<MpmModeloBasicoProcedimento> obterListaProcedimentos(MpmModeloBasicoPrescricao modeloBasicoPrescricao) {
		return this.ordenaLista(this.modeloBasicoFacade.obterListaProcedimentos(modeloBasicoPrescricao));
	}

	private List<MpmModeloBasicoProcedimento> ordenaLista(List<MpmModeloBasicoProcedimento> listaModeloBasicoProcedimento) {
		final Collator collator = Collator.getInstance(new Locale("pt", "BR"));
		collator.setStrength(Collator.PRIMARY);
		Collections.sort(listaModeloBasicoProcedimento, new Comparator<MpmModeloBasicoProcedimento>() {
			public int compare(MpmModeloBasicoProcedimento mpmModeloBasicoProcedimento, MpmModeloBasicoProcedimento mpmModeloBasicoProcedimento1) {
				return collator.compare(modeloBasicoFacade.getDescricaoEditadaModeloBasicoProcedimento(mpmModeloBasicoProcedimento),
						modeloBasicoFacade.getDescricaoEditadaModeloBasicoProcedimento(mpmModeloBasicoProcedimento1));
			}
		});
		return listaModeloBasicoProcedimento;
	}

	private List<MpmModeloBasicoModoUsoProcedimento> ordenaListaModoDeUsoProcedimentos(List<MpmModeloBasicoModoUsoProcedimento> listaModeloBasicoModoDeUso) {
		final Collator collator = Collator.getInstance(new Locale("pt", "BR"));
		collator.setStrength(Collator.PRIMARY);
		Collections.sort(listaModeloBasicoModoDeUso, new Comparator<MpmModeloBasicoModoUsoProcedimento>() {
			public int compare(MpmModeloBasicoModoUsoProcedimento mpmModeloBasicoModoUsoProcedimento, MpmModeloBasicoModoUsoProcedimento mpmModeloBasicoModoUsoProcedimento1) {
				return collator.compare(mpmModeloBasicoModoUsoProcedimento.getDescricaoEditada(), mpmModeloBasicoModoUsoProcedimento1.getDescricaoEditada());
			}
		});
		return listaModeloBasicoModoDeUso;
	}

	public void valueChangeTipoProcedimentoEspecial() {
		doTipoProcedimentoEspecial();
	}

	private void doTipoProcedimentoEspecial() {
  		this.setEhTipoEspecialDiverso((DominioTipoProcedimentoEspecial.ESPECIAIS_DIVERSOS == this.getProcedimento().getTipo()));
		this.setEhTipoProcedimentosLeito((DominioTipoProcedimentoEspecial.PROCEDIMENTOS_REALIZADOS_NO_LEITO == this.getProcedimento().getTipo()));
		this.setEhTipoOrtesesProteses((DominioTipoProcedimentoEspecial.ORTESES_PROTESES == this.getProcedimento().getTipo()));
	}

	// Metódo para SuggestionAction da SB de Especiais Diversos
	public List<MpmProcedEspecialDiversos> obterProcedEspecialDiversos(String objPesquisa) {
		return this.prescricaoMedicaFacade.getListaProcedEspecialDiversos(objPesquisa);
	}

	// Metódo para SuggestionAction da SB de Modo Uso Procedimentos Diversos
	public List<MpmTipoModoUsoProcedimento> obterTipoModoUsoProcedimento(String objPesquisa) throws ApplicationBusinessException {
		if (this.procedimento.getProcedEspecial() != null) {
			return this.prescricaoMedicaFacade.getListaTipoModoUsoProcedEspeciaisDiversos(objPesquisa, this.procedimento.getProcedEspecial());
		} else {
			return null;
		}
	}

	// Metódo para SuggestionAction da SB de Procedimentos Realizados no Leito
	public List<MbcProcedimentoCirurgicos> obterProcedRealizadosLeito(String objPesquisa) {
		return this.prescricaoMedicaFacade.getListaProcedimentoCirurgicosRealizadosNoLeito(objPesquisa);
	}

    public List<ScoMaterial> obterOrteseseProteses(String objPesquisa)
            throws ApplicationBusinessException {
        BigDecimal paramVlNumerico = null;

        AghParametros parametro = this.parametroFacade.buscarAghParametro(
                AghuParametrosEnum.GRPO_MAT_ORT_PROT);

        if (parametro != null) {
            paramVlNumerico = parametro.getVlrNumerico();
        }
           List<ScoMaterial> result = this.prescricaoMedicaFacade.obterMateriaisOrteseseProtesesPrescricao(
                   paramVlNumerico, objPesquisa);
        return result;
    }

	public void adicionarModoUso() {
		try {

			if (this.procedimento.getOrteseProtese() != null || this.procedimento.getProcedCirugRealizadosLeito() != null || this.procedimento.getProcedEspecial() != null) {

				if (this.modoUsoProc.getTipoModoUsoProcedimento() == null) {
					throw new ApplicationBusinessException(ManterProcedimentosModeloBasicoControllerExceptionCode.MENSAGEM_MODO_DE_USO_NAO_PREENCHIDO);
				}

				if (this.modoUsoProc.getTipoModoUsoProcedimento().getIndExigeQuantidade() && this.modoUsoProc.getQuantidade() == null) {

					apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_MODO_USO_EXIGE_QUANTIDADE");
					return;
				}

				if (this.modoUsoProc.getQuantidade() != null && this.modoUsoProc.getQuantidade() == (short) 0) {
					apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_MODO_USO_EXIGE_QUANTIDADE_MAIOR_ZERO");
					return;
				}

			} else {
				throw new ApplicationBusinessException(ManterProcedimentosModeloBasicoControllerExceptionCode.MENSAGEM_PROCEDIMENTO_NAO_PREENCHIDO);
			}

			MpmModeloBasicoModoUsoProcedimentoId idNovo = new MpmModeloBasicoModoUsoProcedimentoId(this.seqModelo, null, this.modoUsoProc.getTipoModoUsoProcedimento().getId().getPedSeq(),
					this.modoUsoProc.getTipoModoUsoProcedimento().getId().getSeqp());

			this.modoUsoProc.setServidor(this.servidorLogadoFacade.obterServidorLogado());
			this.modoUsoProc.setId(idNovo);
			this.modoUsoProc.setModeloBasicoProcedimento(this.modeloBasicoProcedimento);

			if (this.listaModoUsoParaExclusao.contains(this.modoUsoProc)) {
				this.listaModoUsoParaExclusao.remove(this.modoUsoProc);
			}

			// Para comparar o objeto, seto no modo de uso a seq do modelo de
			// procedimento,
			// pois não é permitido inserir o mesmo modo de uso mais de uma vez
			// para o mesmo procedimento
			if (!this.listaModoUsoProdedimentoEspecial.isEmpty()) {
				this.modoUsoProc.getId().setModeloBasicoProcedimentoSeq(this.listaModoUsoProdedimentoEspecial.get(0).getId().getModeloBasicoProcedimentoSeq());
			}

			if (!this.listaModoUsoProdedimentoEspecial.contains(this.modoUsoProc)) {
				this.listaModoUsoProdedimentoEspecial.add(this.modoUsoProc);
			} else {
				this.limparModoDeUso();
				throw new ApplicationBusinessException(ManterProcedimentosModeloBasicoControllerExceptionCode.MENSAGEM_MODO_DE_USO_JA_EXISTE);
			}
			// Seq do modelo de procedimento setada para valor original, esta
			// seq sera atribuida na gravação do modelo,
			// é uma sequence de banco
			this.modoUsoProc.getId().setModeloBasicoProcedimentoSeq(null);
			this.limparModoDeUso();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void preparaAlterarModoUso(MpmModeloBasicoModoUsoProcedimento mpmModeloBasicoModoUsoProcedimento) throws CloneNotSupportedException {
		this.alteraModoUso = true;
		this.setModoUsoProc(mpmModeloBasicoModoUsoProcedimento);
	}

	public void alterarModoUso() {
        modoUsoProc.getId().setTipoModoUsoProcedimentoSeq(modoUsoProc.getTipoModoUsoProcedimento().getId().getPedSeq());
        modoUsoProc.getId().setTipoModoUsoSeqp(modoUsoProc.getTipoModoUsoProcedimento().getId().getSeqp());
		this.limparModoDeUso();
	}

	public List<MpmModeloBasicoModoUsoProcedimento> obterListaModoDeUsoDoModelo(MpmModeloBasicoProcedimento mpmModeloBasicoProcedimento) {
		try {
			if (mpmModeloBasicoProcedimento == null) {
				throw new ApplicationBusinessException(ManterProcedimentosModeloBasicoControllerExceptionCode.MENSAGEM_MODELO_PROCEDIMENTO_NAO_INFORMADO);
			}

			List<MpmModeloBasicoModoUsoProcedimento> list = this.modeloBasicoFacade.obterListaModoDeUsoDoModelo(mpmModeloBasicoProcedimento);

			return this.ordenaListaModoDeUsoProcedimentos(list);

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e); // trata erro de parâmetro de sistema
		}
		return null;
	}

	/*
	 * public void editarModoUso(MpmModeloBasicoModoUsoProcedimento modoUsoVO) {
	 * // modoUsoVO.setEmEdicao(Boolean.TRUE); this.setModoUsoProc(modoUsoVO); }
	 */

	/*
	 * public void excluirModoUso(ModoUsoProcedimentoEspecialVO modoUsoVO) { if
	 * (modoUsoVO.getEmEdicao()) { this.initModoUsoProc(); }
	 * removerModoUso(modoUsoVO);
	 * this.getListaModoUsoParaExclusao().add(modoUsoVO); }
	 */

	// private List<MpmModeloBasicoModoUsoProcedimento>
	// getListaModoUsoParaExclusao() {
	// if (this.listaModoUsoParaExclusao == null) {
	// this.listaModoUsoParaExclusao = new
	// LinkedList<MpmModeloBasicoModoUsoProcedimento>();
	// }
	// return this.listaModoUsoParaExclusao;
	// }

	public void gravar() {
		try {

			if (this.altera) {

				alterar(this.modeloBasicoProcedimento);

			} else {

				MpmModeloBasicoProcedimentoId id = new MpmModeloBasicoProcedimentoId(this.seqModelo, null);

				MpmModeloBasicoProcedimento novoMestre = new MpmModeloBasicoProcedimento(id);

				novoMestre.setModeloBasicoPrescricao(this.modeloBasicoPrescricao);
				if(this.procedimento != null && this.procedimento.getInformacaoComplementar() != null){
					novoMestre.setInformacoesComplementares(this.procedimento.getInformacaoComplementar().trim());
				}
				novoMestre.setServidor(this.servidorLogadoFacade.obterServidorLogado());

				if (this.procedimento.getOrteseProtese() != null || this.procedimento.getProcedCirugRealizadosLeito() != null || this.procedimento.getProcedEspecial() != null) {

					// verificar o tipo do procedimento para setar campos...
					if (DominioTipoProcedimentoEspecial.ESPECIAIS_DIVERSOS.equals(this.procedimento.getTipo())) {
						novoMestre.setProcedEspecialDiverso(this.procedimento.getProcedEspecial());

						this.modeloBasicoFacade.inserir(novoMestre, this.listaModoUsoProdedimentoEspecial);

						if (this.listaModoUsoParaExclusao != null && !this.listaModoUsoParaExclusao.isEmpty()) {
							this.excluirModoDeUsoProcedimento(this.listaModoUsoParaExclusao);
						}

					} else if (DominioTipoProcedimentoEspecial.PROCEDIMENTOS_REALIZADOS_NO_LEITO.equals(this.procedimento.getTipo())) {
						novoMestre.setProcedimentoCirurgico(this.procedimento.getProcedCirugRealizadosLeito());

						this.modeloBasicoFacade.inserir(novoMestre, null);

					} else if (DominioTipoProcedimentoEspecial.ORTESES_PROTESES.equals(this.procedimento.getTipo())) {

						if (this.procedimento.getQuantidade() != null) {
							if (this.procedimento.getQuantidade() > 0) {
								novoMestre.setMaterial(this.procedimento.getOrteseProtese());
								novoMestre.setQuantidade(this.procedimento.getQuantidade());

								this.modeloBasicoFacade.inserir(novoMestre, null);
							} else {
								throw new ApplicationBusinessException(ManterProcedimentosModeloBasicoControllerExceptionCode.MENSAGEM_QUANTIDADE_MAIOR_ZERO);
							}
						} else {
							throw new ApplicationBusinessException(ManterProcedimentosModeloBasicoControllerExceptionCode.MENSAGEM_QUANTIDADE_NAO_PREENCHIDA);
						}
					}

					String descricaoEditada = this.modeloBasicoFacade.getDescricaoEditadaModeloBasicoProcedimento(novoMestre);

                    getListaProcedimentoEspecial().add(novoMestre);

                    this.limpar();
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_PROCEDIMENTO_MODELO_BASICO", descricaoEditada);

				} else {
					throw new ApplicationBusinessException(ManterProcedimentosModeloBasicoControllerExceptionCode.MENSAGEM_PROCEDIMENTO_NAO_PREENCHIDO);
				}
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Efetiva a alteração no banco de dados, aplica a validação das regras
	 * 
	 * @param mpmModeloBasicoProcedimento
	 */

	public void alterar(MpmModeloBasicoProcedimento mpmModeloBasicoProcedimento) {
		try {
			mpmModeloBasicoProcedimento.setInformacoesComplementares(this.procedimento.getInformacaoComplementar().trim());
			mpmModeloBasicoProcedimento.setId(this.modeloBasicoProcedimentoId);

			mpmModeloBasicoProcedimento.setModeloBasicoPrescricao(this.modeloBasicoPrescricao);
			mpmModeloBasicoProcedimento.setServidor(this.servidorLogadoFacade.obterServidorLogado());

			if (this.procedimento.getOrteseProtese() != null || this.procedimento.getProcedCirugRealizadosLeito() != null || this.procedimento.getProcedEspecial() != null) {


                if (this.getTipoSelecionado().equals(DominioTipoProcedimentoEspecial.ESPECIAIS_DIVERSOS.toString())) {
					mpmModeloBasicoProcedimento.setProcedEspecialDiverso(this.procedimento.getProcedEspecial());
					mpmModeloBasicoProcedimento.setProcedimentoCirurgico(null);
					mpmModeloBasicoProcedimento.setMaterial(null);
					mpmModeloBasicoProcedimento.setQuantidade(null);

                } else if (this.getTipoSelecionado().equals(DominioTipoProcedimentoEspecial.PROCEDIMENTOS_REALIZADOS_NO_LEITO.toString()))  {
					mpmModeloBasicoProcedimento.setProcedEspecialDiverso(null);
					mpmModeloBasicoProcedimento.setProcedimentoCirurgico(this.procedimento.getProcedCirugRealizadosLeito());
					mpmModeloBasicoProcedimento.setMaterial(null);
					mpmModeloBasicoProcedimento.setQuantidade(null);

                } else if (this.getTipoSelecionado().equals(DominioTipoProcedimentoEspecial.ORTESES_PROTESES.toString()))  {
					if (this.procedimento.getQuantidade() != null) {
						if (this.procedimento.getQuantidade() > 0) {
							mpmModeloBasicoProcedimento.setProcedEspecialDiverso(null);
							mpmModeloBasicoProcedimento.setProcedimentoCirurgico(null);
							mpmModeloBasicoProcedimento.setMaterial(this.procedimento.getOrteseProtese());

							mpmModeloBasicoProcedimento.setQuantidade(this.procedimento.getQuantidade());
						} else {
							throw new ApplicationBusinessException(ManterProcedimentosModeloBasicoControllerExceptionCode.MENSAGEM_QUANTIDADE_MAIOR_ZERO);
						}
					} else {
						throw new ApplicationBusinessException(ManterProcedimentosModeloBasicoControllerExceptionCode.MENSAGEM_QUANTIDADE_NAO_PREENCHIDA);
					}
				}

				mpmModeloBasicoProcedimento.setProcedEspecialDiverso(this.modeloBasicoProcedimento.getProcedEspecialDiverso());

				if (this.listaModoUsoParaExclusao != null && !this.listaModoUsoParaExclusao.isEmpty()) {
					this.excluirModoDeUsoProcedimento(this.listaModoUsoParaExclusao);
				}

                this.modeloBasicoFacade.removeListaModoDeUsoDoModelo(mpmModeloBasicoProcedimento);
				this.listaProcedimentoEspecial = this.obterListaProcedimentos(this.modeloBasicoPrescricao);
				String descricao = this.modeloBasicoFacade.getDescricaoEditadaModeloBasicoProcedimento(mpmModeloBasicoProcedimento);
				this.modeloBasicoFacade.alterar(mpmModeloBasicoProcedimento, this.listaModoUsoProdedimentoEspecial);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERACAO_PROCEDIMENTO_MODELO_BASICO", descricao);
				this.limpar();

			} else {
				throw new ApplicationBusinessException(ManterProcedimentosModeloBasicoControllerExceptionCode.MENSAGEM_PROCEDIMENTO_NAO_PREENCHIDO);
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String verificaPendencias() {
		if ((this.procedimento.getProcedEspecial() != null) || (this.procedimento.getProcedCirugRealizadosLeito() != null) || (this.procedimento.getOrteseProtese() != null)) {
			confirmaVoltar = true;
			return null;
		}
		confirmaVoltar = false;
		return PAGE_PRESCRICAO_MEDICA_MANTER_ITENS_MODELO_BASICO;
	}

	public String voltar() {
		this.limpar();
		return PAGE_PRESCRICAO_MEDICA_MANTER_ITENS_MODELO_BASICO;
	}

	/**
	 * Prepara o objeto para a alteração na tela
	 * 
	 * @param procedimento
	 */
	public void preparaAlterar(MpmModeloBasicoProcedimento procedimento) {

        this.setTipoSelecionado(null);
		this.procedimentoEdicao = procedimento;

		if (!this.isCampoAlteradoFormularioItem()) {
			editarItem();
		}
	}

	public void editarItem() {

		MpmModeloBasicoProcedimento procedimento = this.prescricaoMedicaFacade.obterModeloBasicoProcedimentoPorChavePrimaria(this.procedimentoEdicao.getId(), true,
				MpmModeloBasicoProcedimento.Fields.PROCED_ESPECIAL_DIVERSO, MpmModeloBasicoProcedimento.Fields.PROCEDIMENTO_CIRURGICO);

		this.seqModelo = procedimento.getId().getModeloBasicoPrescricaoSeq();
		this.seqItemModelo = procedimento.getId().getSeq().intValue();
		this.modeloBasicoProcedimentoId.setModeloBasicoPrescricaoSeq(this.seqModelo);
		this.modeloBasicoProcedimentoId.setSeq(this.seqItemModelo.shortValue());
		this.modeloBasicoProcedimento = procedimento;

        setTipoSelecionadoFromProcedimento(procedimento);

        this.procedimento.setProcedCirugRealizadosLeito(null);
		if (this.getTipoSelecionado().equals(DominioTipoProcedimentoEspecial.ESPECIAIS_DIVERSOS.toString())) {
			this.ehTipoEspecialDiverso = true;
			this.ehTipoOrtesesProteses = false;
			this.ehTipoProcedimentosLeito = false;

			this.procedimento.setTipo(DominioTipoProcedimentoEspecial.ESPECIAIS_DIVERSOS);
			this.procedimento.setProcedEspecial(procedimento.getProcedEspecialDiverso());

		} else if (this.getTipoSelecionado().equals(DominioTipoProcedimentoEspecial.PROCEDIMENTOS_REALIZADOS_NO_LEITO.toString()))  {
			this.ehTipoEspecialDiverso = false;
			this.ehTipoOrtesesProteses = false;
			this.ehTipoProcedimentosLeito = true;

			this.procedimento.setTipo(DominioTipoProcedimentoEspecial.PROCEDIMENTOS_REALIZADOS_NO_LEITO);
			this.procedimento.setProcedCirugRealizadosLeito(procedimento.getProcedimentoCirurgico());

		} else if (this.getTipoSelecionado().equals(DominioTipoProcedimentoEspecial.ORTESES_PROTESES.toString()))  {
			this.ehTipoEspecialDiverso = false;
			this.ehTipoOrtesesProteses = true;
			this.ehTipoProcedimentosLeito = false;

			this.procedimento.setTipo(DominioTipoProcedimentoEspecial.ORTESES_PROTESES);
			this.procedimento.setOrteseProtese(procedimento.getMaterial());
			this.procedimento.setQuantidade(procedimento.getQuantidade());

		}

		this.procedimento.setInformacaoComplementar(procedimento.getInformacoesComplementares());
		this.listaModoUsoProdedimentoEspecial = this.obterListaModoDeUsoDoModelo(procedimento);
		this.altera = true;
	}

    private void setTipoSelecionadoFromProcedimento(MpmModeloBasicoProcedimento procedimento) {
        if (procedimento.getProcedEspecialDiverso() != null){
         this.setTipoSelecionado(DominioTipoProcedimentoEspecial.ESPECIAIS_DIVERSOS);
        }
        if (procedimento.getProcedimentoCirurgico() != null){
            this.setTipoSelecionado(DominioTipoProcedimentoEspecial.PROCEDIMENTOS_REALIZADOS_NO_LEITO);
        }
        if (procedimento.getMaterial() != null){
            this.setTipoSelecionado(DominioTipoProcedimentoEspecial.ORTESES_PROTESES);
        }
    }

    public void atualizaListaExclusao(MpmModeloBasicoModoUsoProcedimento mpmModeloBasicoModoUsoProcedimento) {
		this.listaModoUsoProdedimentoEspecial.remove(mpmModeloBasicoModoUsoProcedimento);
		this.listaModoUsoParaExclusao.add(mpmModeloBasicoModoUsoProcedimento);
	}

	/**
	 * Excluir modos de uso retirados da lista
	 */
	private void excluirModoDeUsoProcedimento(List<MpmModeloBasicoModoUsoProcedimento> mpmModeloBasicoModoUsoProcedimentos) throws BaseException {

		for (MpmModeloBasicoModoUsoProcedimento mpmModeloBasicoModoUsoProcedimento : mpmModeloBasicoModoUsoProcedimentos) {
            this.modeloBasicoFacade.excluir(mpmModeloBasicoModoUsoProcedimento);
		}
	}


	/**
	 * Remove os modelos básicos de procedimentos marcados para exclusao
	 * 
	 * public void excluirProcedimentosSelecionados(){ try{ this.limpar();
	 * Integer nroProcedimentosRemovidos = 0; if(this.procedimentosSelecionados
	 * != null && !this.procedimentosSelecionados.isEmpty()){
	 * for(MpmModeloBasicoProcedimento mpmModeloBasicoProcedimento:
	 * this.listaProcedimentoEspecial){
	 * if(this.procedimentosSelecionados.get(mpmModeloBasicoProcedimento) ==
	 * true){ this.modeloBasicoFacade.excluir(mpmModeloBasicoProcedimento);
	 * nroProcedimentosRemovidos++; } } } this.limpar(); this.seqItemModelo =
	 * null; if (nroProcedimentosRemovidos > 0){ if (nroProcedimentosRemovidos >
	 * 1){ apresentarMsgNegocio(Severity.INFO,
	 * "MENSAGEM_SUCESSO_REMOCAO_MODELO_PROCEDIMENTOS"); }else{
	 * apresentarMsgNegocio
	 * (Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_MODELO_PROCEDIMENTO"); } }else{
	 * apresentarMsgNegocio
	 * (Severity.ERROR,"MENSAGEM_NENHUM_MODELO_PROCEDIMENTO_SELECIONADO"); }
	 * this.listaProcedimentoEspecial =
	 * this.obterListaProcedimentos(this.modeloBasicoPrescricao);
	 * }catch(BaseException e){ apresentarExcecaoNegocio(e); } }
	 */

	/**
	 * Remove os modelos básicos de procedimentos clicados para exclusao
	 */
	public void excluirProcedimentosSelecionados() {
		try {
			Integer nroProcedimentosRemovidos = 0;
			MpmModeloBasicoProcedimento mpmModeloBasicoProcedimento = this.obterModeloBasicoProcedimento(this.seqModelo, this.seqItemModelo);
			if (mpmModeloBasicoProcedimento != null) {
				this.modeloBasicoFacade.excluir(mpmModeloBasicoProcedimento);
				nroProcedimentosRemovidos++;
			}
			this.limpar();
			this.seqItemModelo = null;
			if (nroProcedimentosRemovidos > 0) {
				if (nroProcedimentosRemovidos > 1) {
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_MODELO_PROCEDIMENTOS");
				} else {
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_MODELO_PROCEDIMENTO");
				}
			} else {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_NENHUM_MODELO_PROCEDIMENTO_SELECIONADO");
			}
			this.listaProcedimentoEspecial = this.obterListaProcedimentos(this.modeloBasicoPrescricao);
			this.iniciar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void cancelarModal(){
		this.seqModelo = null;
		this.seqItemModelo = null;
	}



	public void limpar() {
		
		this.altera = false;
		this.alteraModoUso = false;
		this.seqItemModelo = null;
		this.listaModoUsoProdedimentoEspecial = new LinkedList<MpmModeloBasicoModoUsoProcedimento>();
		this.listaModoUsoParaExclusao = new LinkedList<MpmModeloBasicoModoUsoProcedimento>();
		this.setProcedimento(new ProcedimentoEspecialVO());
		this.getProcedimento().setTipo(DominioTipoProcedimentoEspecial.ESPECIAIS_DIVERSOS);
        this.setTipoSelecionado(DominioTipoProcedimentoEspecial.ESPECIAIS_DIVERSOS);
        this.setEhTipoEspecialDiverso(true);
        this.setEhTipoOrtesesProteses(false);
        this.setEhTipoProcedimentosLeito(false);
		this.modoUsoProc = new MpmModeloBasicoModoUsoProcedimento();
		this.modeloBasicoProcedimentoId = new MpmModeloBasicoProcedimentoId();
		this.desmarcarAlteracaoCampoFormularioItem();
	}

	public void limparModoDeUso() {
		this.alteraModoUso = false;
		this.modoUsoProc = new MpmModeloBasicoModoUsoProcedimento();
	}

	// marcar e desmarcar alteração
	public void marcarAlteracaoCampoFormularioItem() {
        this.setCampoAlteradoFormularioItem(true);
	}

    public void marcarEspeciaisDiversos(){
        this.tipoSelecionado = DominioTipoProcedimentoEspecial.ESPECIAIS_DIVERSOS.toString();
        marcarAlteracaoCampoFormularioItem();
    }

    public void marcarProcedimentoRealizadoNoLeito(){
        this.tipoSelecionado = DominioTipoProcedimentoEspecial.PROCEDIMENTOS_REALIZADOS_NO_LEITO.toString();
        marcarAlteracaoCampoFormularioItem();
    }

    public void marcarProcedimentoOrteseProtese(){
        this.tipoSelecionado = DominioTipoProcedimentoEspecial.ORTESES_PROTESES.toString();
        marcarAlteracaoCampoFormularioItem();
    }

	public void desmarcarAlteracaoCampoFormularioItem() {
		this.setCampoAlteradoFormularioItem(false);
	}

	public String getDescricaoEditada(Integer modBasicoProcedimentoSeq, Integer modSeq) {
		MpmModeloBasicoProcedimento mpmModeloBasicoProcedimento = this.modeloBasicoFacade.obterModeloBasicoProcedimento(modBasicoProcedimentoSeq, modSeq);

		if (mpmModeloBasicoProcedimento != null) {
			return this.modeloBasicoFacade.getDescricaoEditadaProcedimento(mpmModeloBasicoProcedimento);
		}
		return null;
	}

	public void setListaModoUsoProdedimentoEspecial(List<MpmModeloBasicoModoUsoProcedimento> listaModoUsoProdedimentoEspecial) {
		this.listaModoUsoProdedimentoEspecial = listaModoUsoProdedimentoEspecial;
	}

	public List<MpmModeloBasicoModoUsoProcedimento> getListaModoUsoProdedimentoEspecial() {
		return this.listaModoUsoProdedimentoEspecial;
	}

	public void setModoUsoProc(MpmModeloBasicoModoUsoProcedimento modoUsoProc) {
		this.modoUsoProc = modoUsoProc;
	}

	public MpmModeloBasicoModoUsoProcedimento getModoUsoProc() {
		return this.modoUsoProc;
	}

	public Integer getSeqModelo() {
		return seqModelo;
	}

	public void setSeqModelo(Integer seqModelo) {
		this.seqModelo = seqModelo;
	}

	public Integer getSeqItemModelo() {
		return seqItemModelo;
	}

	public void setSeqItemModelo(Integer seqItemModelo) {
		this.seqItemModelo = seqItemModelo;
	}

	public Boolean getEhTipoEspecialDiverso() {
		return ehTipoEspecialDiverso;
	}

	public void setEhTipoEspecialDiverso(Boolean ehTipoEspecialDiverso) {
		this.ehTipoEspecialDiverso = ehTipoEspecialDiverso;
	}

	public Boolean getEhTipoProcedimentosLeito() {
		return ehTipoProcedimentosLeito;
	}

	public void setEhTipoProcedimentosLeito(Boolean ehTipoProcedimentosLeito) {
		this.ehTipoProcedimentosLeito = ehTipoProcedimentosLeito;
	}

	public Boolean getEhTipoOrtesesProteses() {
		return ehTipoOrtesesProteses;
	}

	public void setEhTipoOrtesesProteses(Boolean ehTipoOrtesesProteses) {
		this.ehTipoOrtesesProteses = ehTipoOrtesesProteses;
	}

	public ProcedimentoEspecialVO getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(ProcedimentoEspecialVO procedimento) {
		this.procedimento = procedimento;
	}

	public Long getProcSeq() {
		return procSeq;
	}

	public void setProcSeq(Long procSeq) {
		this.procSeq = procSeq;
	}

	public MpmModeloBasicoPrescricao getModeloBasicoPrescricao() {
		return modeloBasicoPrescricao;
	}

	public void setModeloBasicoPrescricao(MpmModeloBasicoPrescricao modeloBasicoPrescricao) {
		this.modeloBasicoPrescricao = modeloBasicoPrescricao;
	}

	public void setListaProcedimentoEspecial(List<MpmModeloBasicoProcedimento> listaProcedimentoEspecial) {
		this.listaProcedimentoEspecial = listaProcedimentoEspecial;
	}

	public List<MpmModeloBasicoProcedimento> getListaProcedimentoEspecial() {
		return listaProcedimentoEspecial;
	}

	public Boolean getAltera() {
		return altera;
	}

	public void setAltera(Boolean altera) {
		this.altera = altera;
	}

	public Boolean getAlteraModoUso() {
		return alteraModoUso;
	}

	public void setAlteraModoUso(Boolean alteraModoUso) {
		this.alteraModoUso = alteraModoUso;
	}

	public Map<MpmModeloBasicoProcedimento, Boolean> getProcedimentosSelecionados() {
		return procedimentosSelecionados;
	}

	public void setProcedimentosSelecionados(Map<MpmModeloBasicoProcedimento, Boolean> procedimentosSelecionados) {
		this.procedimentosSelecionados = procedimentosSelecionados;
	}

	public boolean isConfirmaVoltar() {
		return confirmaVoltar;
	}

	public void setConfirmaVoltar(boolean confirmaVoltar) {
		this.confirmaVoltar = confirmaVoltar;
	}

	// Gets
	public void setCampoAlteradoFormularioItem(boolean campoAlteradoFormularioItem) {
		this.campoAlteradoFormularioItem = campoAlteradoFormularioItem;
	}

	public boolean isCampoAlteradoFormularioItem() {
		return campoAlteradoFormularioItem;
	}

    public String getTipoSelecionado() {
        return tipoSelecionado;
    }

    public void setTipoSelecionado(DominioTipoProcedimentoEspecial tipoSelecionado) {
        if (tipoSelecionado != null) {
            this.tipoSelecionado = tipoSelecionado.toString();
            this.getProcedimento().setTipo(tipoSelecionado);
        }
    }
}
