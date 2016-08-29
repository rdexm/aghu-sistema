package br.gov.mec.aghu.sicon.cadastrosbasicos.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoCatalogoSicon;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoServicoSicon;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterServicoSiconController extends ActionController {

	private static final long serialVersionUID = 3386406484346147393L;

	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;

	private ScoServicoSicon scoServicoSicon;

	private Integer codigoSicon;
	private ScoServico servico;
	private ScoCatalogoSicon catalogoSiconServico;
	private DominioSituacao situacao;
	private boolean alterar;
	private String origem;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
	 


		// Caso seja enviado um codigoSicon trata-se de uma edição
		if (codigoSicon != null) {
			scoServicoSicon = cadastrosBasicosSiconFacade.obterServicoSicon(codigoSicon);

			if (scoServicoSicon != null) {

				situacao = scoServicoSicon.getSituacao();
				codigoSicon = scoServicoSicon.getCodigoSicon();
				servico = scoServicoSicon.getServico();

				catalogoSiconServico = cadastrosBasicosSiconFacade.obterCatalogoSicon(codigoSicon);

				this.setAlterar(true);
				
			} else {
				apresentarExcecaoNegocio(new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO));
				scoServicoSicon = new ScoServicoSicon();
				this.setSituacao(DominioSituacao.A);
				this.setAlterar(false);
			}

			// Inclusão de novo Código de Serviço
		} else {
			scoServicoSicon = new ScoServicoSicon();
			this.setSituacao(DominioSituacao.A);
			this.setAlterar(false);
		}
	
	}

	public String gravar() {

		if (catalogoSiconServico == null) {
			this.apresentarMsgNegocio(Severity.INFO, "INFORME_CODIGO_SICON_VALIDO");
		} else {
			this.scoServicoSicon.setCodigoSicon(catalogoSiconServico.getCodigoSicon());
			this.scoServicoSicon.setServico(servico);
			this.scoServicoSicon.setSituacao(situacao);

			try {

				if (isAlterar()) {
					cadastrosBasicosSiconFacade.alterarServicoSicon(scoServicoSicon);
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_SERVICO_SICON");

				} else {
					cadastrosBasicosSiconFacade.inserirServicoSicon(scoServicoSicon);
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_SERVICO_SICON");
				}

				return voltar();

			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		}

		codigoSicon = null;
		servico = null;
		catalogoSiconServico = null;

		return null;
	}

	public List<ScoServico> listarServicosAtivos(String pesquisa) throws BaseException {
		List<ScoServico> servicos = null;

		try {
			servicos = cadastrosBasicosSiconFacade.listarServicosAtivos(pesquisa);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return this.returnSGWithCount(servicos,listarCodigoSiconServicoAtivoCount(pesquisa));
	}

	public Long listarServicosAtivosCount(Object pesquisa) throws BaseException {

		return cadastrosBasicosSiconFacade.listarServicosAtivosCount(pesquisa);
	}

	public List<ScoCatalogoSicon> listarCodigoSiconServicoAtivo(String pesquisa) {
		String strParametro = (String) pesquisa;
		Integer codigo = null;

		if (CoreUtil.isNumeroInteger(strParametro)) {
			codigo = Integer.valueOf(strParametro);
		}

		if (codigo == null) {
			if (strParametro.length() < 3) {
				return null;
			}
		}

		return cadastrosBasicosSiconFacade.listarCatalogoSiconServicoAtivo(pesquisa);
	}

	public Long listarCodigoSiconServicoAtivoCount(String pesquisa) {
		String strParametro = (String) pesquisa;
		Integer codigo = null;

		if (CoreUtil.isNumeroInteger(strParametro)) {
			codigo = Integer.valueOf(strParametro);
		}

		if (codigo == null) {
			if (strParametro.length() < 3) {
				return null;
			}
		}

		return cadastrosBasicosSiconFacade.listarCatalogoSiconServicoAtivoCount(pesquisa);
	}

	public String voltar() {
		codigoSicon = null;
		servico = null;
		catalogoSiconServico = null;

		return origem;

	}

	public boolean isAlterar() {
		return alterar;
	}

	public void setAlterar(boolean alterar) {
		this.alterar = alterar;
	}

	public ScoServicoSicon getScoServicoSicon() {
		return scoServicoSicon;
	}

	public void setScoServicoSicon(ScoServicoSicon scoServicoSicon) {
		this.scoServicoSicon = scoServicoSicon;
	}

	public Integer getCodigoSicon() {
		return codigoSicon;
	}

	public void setCodigoSicon(Integer codigoSicon) {
		this.codigoSicon = codigoSicon;
	}

	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	public ScoCatalogoSicon getCatalogoSiconServico() {
		return catalogoSiconServico;
	}

	public void setCatalogoSiconServico(ScoCatalogoSicon catalogoSiconServico) {
		this.catalogoSiconServico = catalogoSiconServico;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

}
