package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.model.AelServidoresExameUnid;
import br.gov.mec.aghu.model.AelServidoresExameUnidId;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterRestringirPedidoServidorController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -7235033495128242634L;

	private static final String PAGE_EXAMES_MANTER_UNIDADES_EXECUTORAS_EXAMES_CRUD = "exames-manterUnidadesExecutorasExamesCRUD";

	private AelUnfExecutaExames aelUnfExecutaExames;
	private RapServidores servidor; // Especialidade/Servidor

	private List<AelServidoresExameUnid> listaAelServidoresExameUnid;

	private String emaExaSigla;
	private Integer emaManSeq;
	private Short unfSeq;
	private Integer matricula;
	private Short vinCodigo;
	private String servidorDesc;
	private Short tamanhoMatricula;

	private AelServidoresExameUnid servidorRemover;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
    private IParametroFacade parametroFacade;
	
	/**
	 * Chamado no inicio de "cada conversação"
	 */
	public void iniciar() {
		try {
			tamanhoMatricula = parametroFacade.buscarValorShort(AghuParametrosEnum.P_AGHU_TAMANHO_MATRICULA);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
		if (StringUtils.isNotBlank(this.emaExaSigla) && this.emaManSeq != null && this.unfSeq != null) {
			this.aelUnfExecutaExames = this.examesFacade.obterAelUnidadeExecutoraExamesPorID(this.emaExaSigla, this.emaManSeq, this.unfSeq);
			atualizaLista();
		}
	
	}

	public void confirmar() {
		try {
			AelServidoresExameUnidId servUnidId = new AelServidoresExameUnidId();

			servUnidId.setAelUnfExecutaExames(aelUnfExecutaExames);
			servUnidId.setRapServidores(this.servidor);

			AelServidoresExameUnid aelServidoresExameUnid = new AelServidoresExameUnid();
			aelServidoresExameUnid.setId(servUnidId);

			cadastrosApoioExamesFacade.persistirAelServidoresExameUnid(aelServidoresExameUnid);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_PERMISSAO_SERVIDOR");

			atualizaLista();
			atribuirServidor(null);

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void escolherServidor() {
		if (this.matricula != null && this.vinCodigo != null) {
			this.atribuirServidor(this.registroColaboradorFacade.buscaServidor(new RapServidoresId(matricula, vinCodigo)));
		}
	}

	public void atribuirServidor(RapServidores servidor) {
		if (servidor != null) {
			setServidor(servidor);
			setServidorDesc(servidor.getPessoaFisica().getNome());
			setMatricula(servidor.getId().getMatricula());
			setVinCodigo(servidor.getId().getVinCodigo());
		} else {
			setServidor(null);
			setServidorDesc(null);
			setMatricula(null);
			setVinCodigo(null);
		}
	}

	public void atribuirServidor() {
		this.atribuirServidor(this.servidor);
	}

	private void atualizaLista() {
		servidor = null;
		listaAelServidoresExameUnid = this.examesFacade.buscaListaAelServidoresExameUnidPorEmaExaSiglaEmaManSeqUnfSeq(this.emaExaSigla, this.emaManSeq, this.unfSeq);
	}

	public void excluir() {
		try {

			this.cadastrosApoioExamesFacade.removerAelServidoresExameUnid(this.emaExaSigla, this.emaManSeq, this.unfSeq, this.servidorRemover.getId().getRapServidores().getId().getMatricula(),
					this.servidorRemover.getId().getRapServidores().getId().getVinCodigo());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_PERMISSAO_SERVIDOR");
			atualizaLista();
			atribuirServidor(null);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} finally {
			this.servidorRemover = null;
		}
	}

	public String voltar() {
		this.aelUnfExecutaExames = null;
		this.servidor = null;
		this.listaAelServidoresExameUnid = null;
		this.emaExaSigla = null;
		this.emaManSeq = null;
		this.unfSeq = null;
		this.matricula = null;
		this.vinCodigo = null;
		this.servidorDesc = null;
		this.servidorRemover = null;
		return PAGE_EXAMES_MANTER_UNIDADES_EXECUTORAS_EXAMES_CRUD;
	}

	// Metódo para Suggestion Box de Especialidade
	public List<RapServidores> obterServidor(String objPesquisa) {
		try {
			return this.cadastrosApoioExamesFacade.buscaListRapServidoresVAelPessoaServidor(objPesquisa, this.emaExaSigla, this.emaManSeq);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<RapServidores>();
	}

	public String getEmaExaSigla() {
		return emaExaSigla;
	}

	public void setEmaExaSigla(String emaExaSigla) {
		this.emaExaSigla = emaExaSigla;
	}

	public Integer getEmaManSeq() {
		return emaManSeq;
	}

	public void setEmaManSeq(Integer emaManSeq) {
		this.emaManSeq = emaManSeq;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public AelUnfExecutaExames getAelUnfExecutaExames() {
		return aelUnfExecutaExames;
	}

	public void setAelUnfExecutaExames(AelUnfExecutaExames aelUnfExecutaExames) {
		this.aelUnfExecutaExames = aelUnfExecutaExames;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public List<AelServidoresExameUnid> getListaAelServidoresExameUnid() {
		return listaAelServidoresExameUnid;
	}

	public void setListaAelServidoresExameUnid(List<AelServidoresExameUnid> listaAelServidoresExameUnid) {
		this.listaAelServidoresExameUnid = listaAelServidoresExameUnid;
	}

	public String getServidorDesc() {
		return servidorDesc;
	}

	public void setServidorDesc(String servidorDesc) {
		this.servidorDesc = servidorDesc;
	}

	public AelServidoresExameUnid getServidorRemover() {
		return servidorRemover;
	}

	public void setServidorRemover(AelServidoresExameUnid servidorRemover) {
		this.servidorRemover = servidorRemover;
	}

	public Short getTamanhoMatricula() {
		return tamanhoMatricula;
	}

	public void setTamanhoMatricula(Short tamanhoMatricula) {
		this.tamanhoMatricula = tamanhoMatricula;
	}
}