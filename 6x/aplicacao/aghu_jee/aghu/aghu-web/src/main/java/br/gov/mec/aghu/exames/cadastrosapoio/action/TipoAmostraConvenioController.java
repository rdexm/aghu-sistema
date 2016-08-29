package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelTipoAmoExameConv;
import br.gov.mec.aghu.model.AelTipoAmostraExame;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;

public class TipoAmostraConvenioController extends ActionController {

	private static final String CAMPO_OBRIGATORIO = "CAMPO_OBRIGATORIO";

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -7190020676574271972L;

	private static final String PAGE_EXAMES_MANTER_EXAMES_MATERIAL_ANALISE_CRUD = "exames-manterExamesMaterialAnaliseCRUD";

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	// entidade readOnly da tela
	private AelTipoAmostraExame tipoAmostraExame = new AelTipoAmostraExame();

	// entidade a ser persistida
	private AelTipoAmoExameConv tipoAmostraExaConv = new AelTipoAmoExameConv();

	// lista dos registros da tela
	private List<AelTipoAmoExameConv> listaTipoAmostraExaConv = new LinkedList<AelTipoAmoExameConv>();

	// Flags que determinam comportamento da tela
	private Boolean emEdicao = Boolean.FALSE;

	// Parâmetros da conversação
	// private String emaExaSigla;
	// private Integer emaManSeq;
	// private Integer manSeq;
	// private DominioOrigemAtendimento origemAtendimento;

	private String voltarPara = "exames-manterExamesMaterialAnaliseCRUD"; // O padrão é voltar para interface de pesquisa

	// parâmetro para exclusão
	private AelTipoAmoExameConv itemExclusao = new AelTipoAmoExameConv();

	private Short cnvCodigo;
	private Short cspSeq;

	/**
	 * Método de inicialização da conversação
	 */
	public String iniciar() {
	 


		// edicao
		if (this.tipoAmostraExame != null) {
			// Ajusta desatachados
			this.tipoAmostraExame = this.examesFacade.obterAelTipoAmostraExame(this.tipoAmostraExame.getId().getEmaExaSigla(), this.tipoAmostraExame.getId().getEmaManSeq(), this.tipoAmostraExame
					.getId().getManSeq(), this.tipoAmostraExame.getId().getOrigemAtendimento());
			this.listaTipoAmostraExaConv = this.cadastrosApoioExamesFacade.listarAelTipoAmoExameConvPorTipoAmostraExame(this.tipoAmostraExame.getId().getEmaExaSigla(), this.tipoAmostraExame.getId()
					.getEmaManSeq(), this.tipoAmostraExame.getId().getManSeq(), this.tipoAmostraExame.getId().getOrigemAtendimento());
		}

		if (PAGE_EXAMES_MANTER_EXAMES_MATERIAL_ANALISE_CRUD.equalsIgnoreCase(this.voltarPara) && (this.tipoAmostraExame == null || this.tipoAmostraExame.getId() == null)) {
			this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_NECESSARIO_GRAVAR_TIPO_AMOSTRA_CONVENIO");
			this.limparParametros();
			return this.voltarPara;
		}

		return null;
	
	}

	/**
	 * Método que insere ou atualiza um registro<br>
	 * na tabela AEL_TIPOS_AMOSTRA_EXAMES
	 */
	public void confirmar() {

		/**
		 * Validação dos campos requiredFake
		 */
		if (this.cnvCodigo == null && (tipoAmostraExaConv.getConvSaudePlanos() == null || tipoAmostraExaConv.getConvSaudePlanos().getId().getCnvCodigo() == null)) {
			this.apresentarMsgNegocio("convenio", Severity.ERROR, CAMPO_OBRIGATORIO, "Convênio");
			return;
		}
		if (this.cspSeq == null && (tipoAmostraExaConv.getConvSaudePlanos() == null || tipoAmostraExaConv.getConvSaudePlanos().getId().getSeq() == null)) {
			this.apresentarMsgNegocio("cspSeq", Severity.ERROR, CAMPO_OBRIGATORIO, "Plano");
			return;
		}
		if (this.tipoAmostraExaConv.getConvSaudePlanos() == null) {
			this.apresentarMsgNegocio("sbConvenioPlano", Severity.ERROR, CAMPO_OBRIGATORIO, "Convênio/Plano");
			return;
		}
		if (this.tipoAmostraExaConv.getResponsavelColeta() == null) {
			this.apresentarMsgNegocio("responsavel", Severity.ERROR, CAMPO_OBRIGATORIO, "Responsável");
			return;
		}

		this.tipoAmostraExaConv.setTipoAmostraExame(this.tipoAmostraExame);

		try {
			if (!this.emEdicao) {
				this.iniciar();
				this.cadastrosApoioExamesFacade.inserirAelTipoAmoExameConv(this.tipoAmostraExaConv);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERIR_TIPO_AMOSTRA_CONVENIO", this.tipoAmostraExaConv.getConvSaudePlanos().getDescricaoCompleta());
			} else {
				this.cadastrosApoioExamesFacade.atualizarAelTipoAmoExameConv(this.tipoAmostraExaConv);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERAR_TIPO_AMOSTRA_CONVENIO", this.tipoAmostraExaConv.getConvSaudePlanos().getDescricaoCompleta());
			}
			this.limpar();
			this.iniciar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Cancela a edição da tela
	 */
	public void cancelarEdicao() {
		this.limpar();
		this.iniciar();
	}

	/**
	 * Limpa os dados dos campos na tela
	 */
	public void limpar() {
		this.tipoAmostraExaConv = new AelTipoAmoExameConv();
		this.cnvCodigo = null;
		this.cspSeq = null;
		this.emEdicao = Boolean.FALSE;
	}

	/**
	 * Método de edição da tela
	 * 
	 */
	public void editar() {
		this.emEdicao = Boolean.TRUE;
	}

	/**
	 * Método que exclui registros da <br>
	 * tabela AEL_TIPOS_AMOSTRA_EXAMES
	 */
	public void excluir() {

		if (verificarAlteradoOutroUsuario(this.itemExclusao)) {
			this.iniciar();
			return;
		}

		try {
			this.cadastrosApoioExamesFacade.excluirAelTipoAmoExameConv(this.itemExclusao);
			// this.cadastrosApoioExamesFacade.flush();
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUIR_TIPO_AMOSTRA_CONVENIO", this.itemExclusao.getConvSaudePlanos().getDescricaoCompleta());
			this.limpar();
			this.iniciar();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {
		String retorno = this.voltarPara;
		this.limparParametros();
		this.limpar();
		//this.cancelarEdicao();
		return retorno;
	}

	private void limparParametros() {
		this.tipoAmostraExame = null;
		this.tipoAmostraExaConv = new AelTipoAmoExameConv();
		this.listaTipoAmostraExaConv = new LinkedList<AelTipoAmoExameConv>();
		this.emEdicao = Boolean.FALSE;
		this.voltarPara = "exames-manterExamesMaterialAnaliseCRUD"; // O padrão é voltar para interface de pesquisa
		this.itemExclusao = new AelTipoAmoExameConv();
		this.cnvCodigo = null;
		this.cspSeq = null;
	}

	/**
	 * Verifica se o item AelPacUnidFuncionais foi selecionado na lista
	 * 
	 * @param item
	 * @return
	 */
	public boolean isItemSelecionado(final AelTipoAmoExameConv item) {
		if (this.tipoAmostraExaConv != null && this.tipoAmostraExaConv.equals(item)) {
			return true;
		}
		return false;
	}

	/**
	 * Método de busca da suggestion de convenio/plano
	 * 
	 * @param parametro
	 * @return
	 */
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(String parametro) {
		return this.returnSGWithCount(this.cadastrosApoioExamesFacade.pesquisarConvenioSaudePlanos((String) parametro),pesquisarConvenioSaudePlanosCount(parametro));
	}

	public Long pesquisarConvenioSaudePlanosCount(String parametro) {
		return this.cadastrosApoioExamesFacade.pesquisarConvenioSaudePlanosCount((String) parametro);
	}

	/**
	 * Método que atribui convenio e plano <br>
	 * a partir da escolha da suggestion de convenio/plano
	 */
	public void atribuirConvenioPlano() {
		this.cnvCodigo = this.tipoAmostraExaConv.getConvSaudePlanos().getId().getCnvCodigo();
		this.cspSeq = this.tipoAmostraExaConv.getConvSaudePlanos().getId().getSeq().shortValue();
	}

	/**
	 * Método que remove os valores associados<br>
	 * a partir da escolha da suggestion de convenio/plano
	 */
	public void removerConvenioPlano() {
		this.cnvCodigo = null;
		this.cspSeq = null;
	}

	/**
	 * Método que obtém convenio/plano
	 */
	public void obterConvenioPlano() {
		Byte seqConvenioSaudePlano = null;
		Short codConvenioSaude = null;

		if (this.cspSeq != null) {
			seqConvenioSaudePlano = this.cspSeq.byteValue();
		}

		if (this.cnvCodigo != null) {
			codConvenioSaude = this.cnvCodigo;
		}

		final FatConvenioSaudePlano convenioPlano = this.cadastrosApoioExamesFacade.obterPlanoPorId(seqConvenioSaudePlano, codConvenioSaude);
		if (convenioPlano != null) {
			this.cnvCodigo = convenioPlano.getId().getCnvCodigo();
			this.cspSeq = convenioPlano.getId().getSeq().shortValue();
		}
		this.tipoAmostraExaConv.setConvSaudePlanos(convenioPlano);
	}

	/** GET/SET **/
	public AelTipoAmoExameConv getTipoAmostraExaConv() {
		return tipoAmostraExaConv;
	}

	public void setTipoAmostraExaConv(AelTipoAmoExameConv tipoAmostraExaConv) {
		this.tipoAmostraExaConv = tipoAmostraExaConv;
	}

	public Boolean getEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public AelTipoAmoExameConv getItemExclusao() {
		return itemExclusao;
	}

	public void setItemExclusao(AelTipoAmoExameConv itemExclusao) {
		this.itemExclusao = itemExclusao;
	}

	public String getCampoExame() {
		String exameDesc = null;

		if (this.tipoAmostraExame != null) {
			exameDesc = this.tipoAmostraExame.getId().getEmaExaSigla();
			if (this.tipoAmostraExame.getExamesMaterialAnalise() != null && this.tipoAmostraExame.getExamesMaterialAnalise().getAelExames() != null) {
				exameDesc = exameDesc.concat(" - ").concat(this.tipoAmostraExame.getExamesMaterialAnalise().getAelExames().getDescricaoUsual());
			}
		}

		return exameDesc;
	}

	public String getCampoMaterial() {
		String materialDesc = null;

		if (this.tipoAmostraExame != null) {
			materialDesc = this.tipoAmostraExame.getId().getEmaManSeq().toString();
			if (this.tipoAmostraExame.getMaterialAnalise() != null) {
				materialDesc = materialDesc.concat(" - ").concat(this.tipoAmostraExame.getMaterialAnalise().getDescricao());
			}
		}

		return materialDesc;
	}

	private boolean verificarAlteradoOutroUsuario(AelTipoAmoExameConv entidade) {
		if (entidade == null || this.cadastrosApoioExamesFacade.obterAelTipoAmoExameConvPorId(entidade.getId()) == null) {
			apresentarMsgNegocio(Severity.INFO, "REGISTRO_NULO_EXCLUSAO");
			return true;
		}
		return false;
	}

	// public String getEmaExaSigla() {
	// return emaExaSigla;
	// }
	//
	// public void setEmaExaSigla(String emaExaSigla) {
	// this.emaExaSigla = emaExaSigla;
	// }
	//
	// public Integer getEmaManSeq() {
	// return emaManSeq;
	// }
	//
	// public void setEmaManSeq(Integer emaManSeq) {
	// this.emaManSeq = emaManSeq;
	// }
	//
	// public Integer getManSeq() {
	// return manSeq;
	// }
	//
	// public void setManSeq(Integer manSeq) {
	// this.manSeq = manSeq;
	// }
	//
	// public DominioOrigemAtendimento getOrigemAtendimento() {
	// return origemAtendimento;
	// }
	//
	// public void setOrigemAtendimento(DominioOrigemAtendimento origemAtendimento) {
	// this.origemAtendimento = origemAtendimento;
	// }

	public AelTipoAmostraExame getTipoAmostraExame() {
		return tipoAmostraExame;
	}

	public void setTipoAmostraExame(AelTipoAmostraExame tipoAmostraExame) {
		this.tipoAmostraExame = tipoAmostraExame;
	}

	public List<AelTipoAmoExameConv> getListaTipoAmostraExaConv() {
		return listaTipoAmostraExaConv;
	}

	public void setListaTipoAmostraExaConv(List<AelTipoAmoExameConv> listaTipoAmostraExaConv) {
		this.listaTipoAmostraExaConv = listaTipoAmostraExaConv;
	}

	public Short getCnvCodigo() {
		return cnvCodigo;
	}

	public void setCnvCodigo(Short cnvCodigo) {
		this.cnvCodigo = cnvCodigo;
	}

	public Short getCspSeq() {
		return cspSeq;
	}

	public void setCspSeq(Short cspSeq) {
		this.cspSeq = cspSeq;
	}

}
