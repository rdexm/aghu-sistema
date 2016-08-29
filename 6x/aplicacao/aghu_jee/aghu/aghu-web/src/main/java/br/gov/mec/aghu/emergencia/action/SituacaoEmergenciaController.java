package br.gov.mec.aghu.emergencia.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.configuracao.vo.Especialidade;
import br.gov.mec.aghu.dominio.DominioCaracteristicaEmergencia;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.MamCaractSitEmerg;
import br.gov.mec.aghu.model.MamCaractSitEmergId;
import br.gov.mec.aghu.model.MamSituacaoEmergencia;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller das ações da pagina de criação e edição de situações da emergência.
 * 
 * @author luismoura
 * 
 */
public class SituacaoEmergenciaController extends ActionController {
	private static final long serialVersionUID = 5680509478357451857L;

	private final String PAGE_LIST_SIT_EMERG = "situacaoEmergenciaList";

	@Inject
	private IEmergenciaFacade emergenciaFacade;

	private MamSituacaoEmergencia situacaoEmergencia;
	private Boolean indSituacao;
	private Boolean indTriagem;

	private Boolean habilitaCaracteristicaEmergencia;

	private MamCaractSitEmerg mamCaractSitEmerg;
	private DominioCaracteristicaEmergencia caracteristicaEmergencia;
	private Boolean indSituacaoCarac;
	private List<MamCaractSitEmerg> dataModel = new ArrayList<MamCaractSitEmerg>();

	@PostConstruct
	public void init() {
		begin(conversation);
	}

	public void inicio() {
		if (this.situacaoEmergencia != null && this.situacaoEmergencia.getSeq() != null) {
			setIndSituacao(this.situacaoEmergencia.getIndSituacao().isAtivo());
			setIndTriagem(this.situacaoEmergencia.getIndUsoTriagem());
			setHabilitaCaracteristicaEmergencia(Boolean.TRUE);
			this.pesquisar();
		} else {
			this.situacaoEmergencia = new MamSituacaoEmergencia();
			setIndSituacao(Boolean.TRUE);
			setIndTriagem(Boolean.TRUE);
			setHabilitaCaracteristicaEmergencia(Boolean.FALSE);
		}
		this.limparCaractSitEmerg();

	}

	private void pesquisar() {
		this.dataModel = emergenciaFacade.pesquisarCaracteristicaSituacaoEmergencia(situacaoEmergencia.getSeq());
	}

	/**
	 * Ação do primeiro botão GRAVAR (situações de emergência) da pagina de cadastro de situações da emergência.
	 */
	public void confirmar() {
		try {
			situacaoEmergencia.setIndSituacao(DominioSituacao.getInstance(this.indSituacao));
			situacaoEmergencia.setIndUsoTriagem(this.indTriagem);

			boolean create = this.situacaoEmergencia.getSeq() == null;

			this.emergenciaFacade.persistirMamSituacaoEmergencia(situacaoEmergencia);

			this.setHabilitaCaracteristicaEmergencia(Boolean.TRUE);

			this.pesquisar();

			if (create) {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_SITUACAO");
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_SITUACAO");
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do segundo botão GRAVAR (características de situação de emergência) da pagina de cadastro de situações da emergência.
	 */
	public void gravarCaractSitEmerg() {
		try {
			mamCaractSitEmerg = new MamCaractSitEmerg();
			mamCaractSitEmerg.setId(new MamCaractSitEmergId(situacaoEmergencia.getSeq(), caracteristicaEmergencia));
			mamCaractSitEmerg.setMamSituacaoEmergencia(situacaoEmergencia);
			mamCaractSitEmerg.setIndSituacao(DominioSituacao.getInstance(this.getIndSituacaoCarac()));

			this.emergenciaFacade.inserirMamCaractSitEmerg(mamCaractSitEmerg);

			this.limparCaractSitEmerg();

			this.pesquisar();

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_CARACT_SITUACAO");

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	/**
	 * Ação do segundo botão ATIVAR/INATIVAR da pagina de cadastro de situações da emergência.
	 */
	public void inativarCaracSitEmergencia() {
		try {
			if (mamCaractSitEmerg.getId() != null) {
				this.emergenciaFacade.ativarInativarMamCaractSitEmerg(mamCaractSitEmerg);
				this.pesquisar();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_CARACT_SITUACAO");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Ação do segundo botão EXCLUIR da pagina de cadastro de situações da emergência.
	 */
	public void excluirCaracSitEmergencia() {
		try {
			if (mamCaractSitEmerg.getId() != null) {
				this.emergenciaFacade.excluirMamCaractSitEmerg(mamCaractSitEmerg);
				this.pesquisar();
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_CARACT_SIT_EMERG");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	private void limparCaractSitEmerg() {
		this.setMamCaractSitEmerg(null);
		this.setCaracteristicaEmergencia(null);
		this.setIndSituacaoCarac(true);

	}

	/**
	 * Ação do segundo botão VOLTAR da pagina de cadastro de situações da emergência.
	 */
	public String cancelar() {
		this.situacaoEmergencia = new MamSituacaoEmergencia();
		setIndSituacao(Boolean.TRUE);
		setIndTriagem(Boolean.TRUE);
		return PAGE_LIST_SIT_EMERG;
	}

	public List<Especialidade> pesquisarEspecialidade(Object objPesquisa) {
		return this.emergenciaFacade.pesquisarEspecialidade(objPesquisa);
	}

	public Boolean getBolIndSituacao(DominioSituacao indSituacao) {
		return indSituacao != null && indSituacao.equals(DominioSituacao.A);
	}

	public Especialidade obterEspecialidade(Short seq) {
		return this.emergenciaFacade.obterEspecialidade(seq);
	}

	// ### GETs e SETs ###

	public MamSituacaoEmergencia getSituacaoEmergencia() {
		return situacaoEmergencia;
	}

	public void setSituacaoEmergencia(MamSituacaoEmergencia situacaoEmergencia) {
		this.situacaoEmergencia = situacaoEmergencia;
	}

	public Boolean getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(Boolean indSituacao) {
		this.indSituacao = indSituacao;
	}

	public DominioCaracteristicaEmergencia getCaracteristicaEmergencia() {
		return caracteristicaEmergencia;
	}

	public void setCaracteristicaEmergencia(DominioCaracteristicaEmergencia caracteristicaEmergencia) {
		this.caracteristicaEmergencia = caracteristicaEmergencia;
	}

	public List<MamCaractSitEmerg> getDataModel() {
		return dataModel;
	}

	public void setDataModel(List<MamCaractSitEmerg> dataModel) {
		this.dataModel = dataModel;
	}

	public MamCaractSitEmerg getMamCaractSitEmerg() {
		return mamCaractSitEmerg;
	}

	public void setMamCaractSitEmerg(MamCaractSitEmerg mamCaractSitEmerg) {
		this.mamCaractSitEmerg = mamCaractSitEmerg;
	}

	public Boolean getIndSituacaoCarac() {
		return indSituacaoCarac;
	}

	public void setIndSituacaoCarac(Boolean indSituacaoCarac) {
		this.indSituacaoCarac = indSituacaoCarac;
	}

	public Boolean getHabilitaCaracteristicaEmergencia() {
		return habilitaCaracteristicaEmergencia;
	}

	public void setHabilitaCaracteristicaEmergencia(Boolean habilitaCaracteristicaEmergencia) {
		this.habilitaCaracteristicaEmergencia = habilitaCaracteristicaEmergencia;
	}

	public Boolean getIndTriagem() {
		return indTriagem;
	}

	public void setIndTriagem(Boolean indTriagem) {
		this.indTriagem = indTriagem;
	}
}
