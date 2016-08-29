package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioFuncaoPatologista;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelApXPatologista;
import br.gov.mec.aghu.model.AelApXPatologistaId;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class TrocarResidentePatologistaController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final String PAGE_EXAMES_LISTA_REALIZACAO_EXAMES_PATOLOGIA = "exames-listaRealizacaoExamesPatologia";

	private static final long serialVersionUID = -1221282977588252360L;

	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	private Long lumSeq;
	private Integer matricula;
	private Integer exameSeq;
	private String trocar;

	private AelApXPatologista anatoXPatologista;
	private AelAnatomoPatologico anatomoPatologico;
	private AelConfigExLaudoUnico exame;
	private AelPatologista patologistaResp;
	private AelPatologista novoPatologistaResp;

	public enum TrocarResidentePatologistaControllerExceptionCode implements BusinessExceptionCode {
		PATOLOGISTA_RESIDENTE_OBRIGATORIO, MESMO_PATOLOGISTA_RESIDENTE_SELECIONADO
	}

	public void iniciar() {
	 


		RapServidores servidorLogado = null;
		try {
			servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e) {
			servidorLogado = null;
		}

		if (this.lumSeq != null && this.exameSeq != null && trocar != null) {

			if("P".equals(trocar)) {
				anatoXPatologista = this.examesPatologiaFacade.obterAelApXPatologistaPorSeqAnatoPatologicoMatriculaEFuncao(lumSeq, matricula,
						new DominioFuncaoPatologista[] { DominioFuncaoPatologista.C, DominioFuncaoPatologista.P }, null);
				novoPatologistaResp = examesPatologiaFacade.obterAelPatologistaAtivoPorServidorEFuncao(servidorLogado, DominioFuncaoPatologista.C, DominioFuncaoPatologista.P);
			}
			else if("R".equals(trocar)) {
				anatoXPatologista = this.examesPatologiaFacade.obterAelApXPatologistaPorSeqAnatoPatologicoMatriculaEFuncao(lumSeq, matricula,
						new DominioFuncaoPatologista[] { DominioFuncaoPatologista.R }, null);
				novoPatologistaResp = examesPatologiaFacade.obterAelPatologistaAtivoPorServidorEFuncao(servidorLogado, DominioFuncaoPatologista.R);
			}

			if (anatoXPatologista != null && anatoXPatologista.getAelPatologista() != null) {
				patologistaResp = this.examesPatologiaFacade.obterPatologistaPorChavePrimaria(anatoXPatologista.getAelPatologista().getSeq());
			}
		}
		anatomoPatologico = this.examesPatologiaFacade.obterAelAnatomoPatologico(lumSeq);
		exame = this.examesPatologiaFacade.obterConfigExameLaudoUncioPorChavePrimaria(exameSeq);
	
	}

	public String gravar() {
		try {
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());

			if (novoPatologistaResp == null) {
				throw new ApplicationBusinessException(TrocarResidentePatologistaControllerExceptionCode.PATOLOGISTA_RESIDENTE_OBRIGATORIO);
			}

			if (novoPatologistaResp.equals(patologistaResp)) {
				throw new ApplicationBusinessException(TrocarResidentePatologistaControllerExceptionCode.MESMO_PATOLOGISTA_RESIDENTE_SELECIONADO);
			}

			AelApXPatologista novoAnatoXPatologista = new AelApXPatologista();
			novoAnatoXPatologista.setId(new AelApXPatologistaId(anatomoPatologico.getSeq(), novoPatologistaResp.getSeq()));

			if (anatoXPatologista != null) {
				this.examesPatologiaFacade.excluirAelApXPatologistaPorPatologista(anatoXPatologista.getId().getLuiSeq(), anatoXPatologista.getId().getLumSeq());
			}

			this.examesPatologiaFacade.persistirAelApXPatologista(novoAnatoXPatologista);
			
			this.examesPatologiaFacade.removeAdicionaPatologistaLaudo(anatomoPatologico, patologistaResp, novoPatologistaResp, servidorLogado);

			// this.reiniciarPaginator(ListaRealizacaoExamesPatologiaController.class);

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_TROCA_RESIDENTE_PATOLOGISTA_SUCESSO");
			
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
		this.limparParametros();
		return PAGE_EXAMES_LISTA_REALIZACAO_EXAMES_PATOLOGIA;
	}


	public List<AelPatologista> pesquisarPatologistasPorFuncao(final String valor) {
		if ("P".equals(trocar)) {
			return this.examesPatologiaFacade.pesquisarPatologistasPorFuncao(valor, new DominioFuncaoPatologista[] { DominioFuncaoPatologista.C, DominioFuncaoPatologista.P });
		} else if ("R".equals(trocar)) {
			return this.examesPatologiaFacade.pesquisarPatologistasPorFuncao(valor, new DominioFuncaoPatologista[] { DominioFuncaoPatologista.R });
		}
		return null;
	}
	
	public String cancelar(){
		this.limparParametros();
		return PAGE_EXAMES_LISTA_REALIZACAO_EXAMES_PATOLOGIA;
	}

	private void limparParametros(){
		this.lumSeq= null;
		this.matricula= null;
		this.exameSeq= null;
		this.trocar= null;
		this.anatoXPatologista= null;
		this.anatomoPatologico= null;
		this.exame= null;
		this. patologistaResp= null;
		this.novoPatologistaResp= null;		
	}

	public Long getLumSeq() {
		return lumSeq;
	}

	public void setLumSeq(Long lumSeq) {
		this.lumSeq = lumSeq;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Integer getExameSeq() {
		return exameSeq;
	}

	public void setExameSeq(Integer exameSeq) {
		this.exameSeq = exameSeq;
	}

	public AelApXPatologista getAnatoXPatologista() {
		return anatoXPatologista;
	}

	public void setAnatoXPatologista(AelApXPatologista anatoXPatologista) {
		this.anatoXPatologista = anatoXPatologista;
	}

	public AelAnatomoPatologico getAnatomoPatologico() {
		return anatomoPatologico;
	}

	public void setAnatomoPatologico(AelAnatomoPatologico anatomoPatologico) {
		this.anatomoPatologico = anatomoPatologico;
	}

	public AelConfigExLaudoUnico getExame() {
		return exame;
	}

	public void setExame(AelConfigExLaudoUnico exame) {
		this.exame = exame;
	}

	public AelPatologista getPatologistaResp() {
		return patologistaResp;
	}

	public void setPatologistaResp(AelPatologista patologistaResp) {
		this.patologistaResp = patologistaResp;
	}

	public AelPatologista getNovoPatologistaResp() {
		return novoPatologistaResp;
	}

	public void setNovoPatologistaResp(AelPatologista novoPatologistaResp) {
		this.novoPatologistaResp = novoPatologistaResp;
	}

	public String getTrocar() {
		return trocar;
	}

	public void setTrocar(String trocar) {
		this.trocar = trocar;
	}
}
