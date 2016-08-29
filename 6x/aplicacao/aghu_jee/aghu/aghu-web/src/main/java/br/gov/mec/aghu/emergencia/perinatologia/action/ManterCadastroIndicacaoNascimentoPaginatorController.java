package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.beanutils.PropertyUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoIndicacaoNascimento;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.McoIndicacaoNascimento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;

public class ManterCadastroIndicacaoNascimentoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 5912648867720596933L;
	
	private static final String PAGE_MANTER_CADASTRO_IND_NASCIMENTO = "manterCadastroIndicacaoNascimento";

	@Inject 
	private IEmergenciaFacade emergenciaFacade;
	
	

	
	private IPermissionService getPermissionService() {
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}
	
	@Inject @Paginator
	private DynamicDataModel<McoIndicacaoNascimento> dataModel;

	private McoIndicacaoNascimento indicacaoNascimento;
	private McoIndicacaoNascimento indicacaoNascimentoOriginal;

	private Integer codigo;
	private String descricao;
	private DominioTipoIndicacaoNascimento tipo;
	private DominioSituacao situacao;
	private Boolean indSituacao;
	
	private boolean permManterIndicacaoNascimentos;
	
	private Boolean modoEdicao = Boolean.FALSE;
	private Boolean modoManter = Boolean.FALSE;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
		
		this.permManterIndicacaoNascimentos = getPermissionService().usuarioTemPermissao(
				obterLoginUsuarioLogado(), "manterIndicacaoNascimentos", "executar");
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<McoIndicacaoNascimento> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc) {
		
		List<McoIndicacaoNascimento> indicacoesNascimento = new ArrayList<McoIndicacaoNascimento>();
		
		indicacoesNascimento = this.emergenciaFacade.pesquisarIndicacoesNascimento(firstResult, maxResult,
				orderProperty, asc, codigo, descricao, tipo, situacao);
		
		return indicacoesNascimento;
	}

	@Override
	public Long recuperarCount() {
		return this.emergenciaFacade.pesquisarIndicacoesNascimentoCount(codigo, descricao, tipo, situacao);
	}
	
	public void limparCamposPesquisa() {
		setCodigo(null);
		setDescricao(null);
		setTipo(null);
		setSituacao(null);
		setIndSituacao(true);
		setIndicacaoNascimento(new McoIndicacaoNascimento());
		getIndicacaoNascimento().setIndSituacao(null);
		setModoEdicao(Boolean.FALSE);
		setModoManter(Boolean.FALSE);
		this.getDataModel().limparPesquisa();
	}
	
	public void pesquisar() {
		setIndicacaoNascimento(new McoIndicacaoNascimento());
		setIndSituacao(true);
		this.getDataModel().reiniciarPaginator();
		setModoManter(Boolean.TRUE);
	}
	
	public String gravar() throws ParseException, BaseException {
		String bundleMsgSucesso = "";
		String retornoGravacao = null;
		try {
			if (getModoEdicao()) {
				bundleMsgSucesso = "MENSAGEM_SUCESSO_ALTERACAO_INDICACAO";
			} else {
				bundleMsgSucesso = "MENSAGEM_SUCESSO_CADASTRO_INDICACAO";
			}
			getIndicacaoNascimento().setIndSituacao(DominioSituacao.getInstance(this.indSituacao));
			this.emergenciaFacade.persistirIndicacaoNascimento(getIndicacaoNascimento(), getIndicacaoNascimentoOriginal());
			this.apresentarMsgNegocio(Severity.INFO, bundleMsgSucesso);
			//Efetua a pesquisa de novo para o registro aparecer atualizado
			pesquisar();
			cancelarEdicao();
		} catch (BaseListException e) {
			getIndicacaoNascimento().setCriadoEm(null);
			this.apresentarExcecaoNegocio(e);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return retornoGravacao;
	}
	
	
	public String redirecionarEdicao(McoIndicacaoNascimento indicacaoNascimento) throws ReflectiveOperationException {
		setIndicacaoNascimento(indicacaoNascimento);
		setIndSituacao(indicacaoNascimento.getIndSituacao() != null &&
				indicacaoNascimento.getIndSituacao().equals(DominioSituacao.A));
		// Faz uma cópia de McoIndicacaoNascimento para testar se algum campo será alterado.
		setIndicacaoNascimentoOriginal(new McoIndicacaoNascimento());
		
			PropertyUtils.copyProperties(getIndicacaoNascimentoOriginal(), getIndicacaoNascimento());
	
		setModoEdicao(Boolean.TRUE);
		return PAGE_MANTER_CADASTRO_IND_NASCIMENTO;
	}
	
	public void cancelarEdicao() {
		setIndicacaoNascimento(new McoIndicacaoNascimento());
		setIndicacaoNascimentoOriginal(new McoIndicacaoNascimento());
		setIndSituacao(true);
		setModoEdicao(Boolean.FALSE);
		this.getDataModel().reiniciarPaginator();
	}
	
	/*
	 * Getters and Setters
	 */
	public DynamicDataModel<McoIndicacaoNascimento> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<McoIndicacaoNascimento> dataModel) {
		this.dataModel = dataModel;
	}

	public McoIndicacaoNascimento getIndicacaoNascimento() {
		return indicacaoNascimento;
	}

	public void setIndicacaoNascimento(McoIndicacaoNascimento indicacaoNascimento) {
		this.indicacaoNascimento = indicacaoNascimento;
	}

	public McoIndicacaoNascimento getIndicacaoNascimentoOriginal() {
		return indicacaoNascimentoOriginal;
	}

	public void setIndicacaoNascimentoOriginal(
			McoIndicacaoNascimento indicacaoNascimentoOriginal) {
		this.indicacaoNascimentoOriginal = indicacaoNascimentoOriginal;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioTipoIndicacaoNascimento getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoIndicacaoNascimento tipo) {
		this.tipo = tipo;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public Boolean getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(Boolean indSituacao) {
		this.indSituacao = indSituacao;
	}

	public boolean isPermManterIndicacaoNascimentos() {
		return permManterIndicacaoNascimentos;
	}

	public void setPermManterIndicacaoNascimentos(
			boolean permManterIndicacaoNascimentos) {
		this.permManterIndicacaoNascimentos = permManterIndicacaoNascimentos;
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public Boolean getModoManter() {
		return modoManter;
	}

	public void setModoManter(Boolean modoManter) {
		this.modoManter = modoManter;
	}
}