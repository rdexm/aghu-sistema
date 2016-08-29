package br.gov.mec.aghu.exames.patologia.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioFuncaoPatologista;
import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.exames.patologia.business.IExamesPatologiaFacade;
import br.gov.mec.aghu.exames.patologia.vo.AelPatologistaLaudoVO;
import br.gov.mec.aghu.exames.patologia.vo.TelaLaudoUnicoVO;
import br.gov.mec.aghu.model.AelCidos;
import br.gov.mec.aghu.model.AelDiagnosticoLaudos;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelNomenclaturaAps;
import br.gov.mec.aghu.model.AelPatologista;
import br.gov.mec.aghu.model.AelPatologistaAps;
import br.gov.mec.aghu.model.AelTopografiaAps;
import br.gov.mec.aghu.model.AelTopografiaCidOs;
import br.gov.mec.aghu.model.AelTopografiaLaudos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

@SuppressWarnings("PMD.AghuTooManyMethods")
public class CadastroLaudoUnicoController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 136429204303812851L;
		
	@EJB
	private IExamesPatologiaFacade examesPatologiaFacade;

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private LaudoUnicoController laudoUnicoController;	

	private AelDiagnosticoLaudos aelDiagnosticoLaudos;

	private AelPatologista aelPatologista;

	private AelTopografiaCidOs aelTopografiaCidOs;

	private AelCidos aelCidO;

	private AghCid aghCid;

	private TelaLaudoUnicoVO tela;

	private String tipoExclusao;

	private final String PANEL_LISTA_DIAGNOSTICO_LAUDOS = "panelListaDiagnosticoLaudos";

	private final String PANEL_LISTA_PATOLOGISTA_AP = "panelListaPatologistaAp";

	private final String PANEL_LISTA_TOPOGRAFIA_LAUDO = "panelListaTopografiaLaudo";

	// atributos para exclusao
	private Long seqExcluirTopografiaLaudoCidOs;

	private Long seqExcluirDiagnosticoLaudos;

	private Integer seqExcluirPatologistaAp;

	private List<AelTopografiaLaudos> listaTopografiaLaudos;

	private List<AelDiagnosticoLaudos> listaDiagnosticoLaudos;

	private List<AelPatologistaLaudoVO> listaPatologistaLaudoVO;

	private Long cidOSeq;

	private Integer seqCid;
	
	private List<AelTopografiaAps> listaTopografiaAp;

	private List<AelNomenclaturaAps> listaNomenclaturaAp;

	private boolean laudoAntigo;	
	
	private Integer topografiaDataTableHeight;
	private Integer diagnosticoDataTableHeight;
	private Integer patologistaDataTableHeight;

	public void inicio(final TelaLaudoUnicoVO tela) {
		this.tela = tela;

		if (tela != null) {
			if(laudoAntigo){
				//consulta topografia
				listaTopografiaAp = examesPatologiaFacade.listarTopografiaApPorLuxSeq(tela.getAelExameAp().getSeq());
				//consulta nomenclatura
				listaNomenclaturaAp = examesPatologiaFacade.listarNomenclaturaApPorLuxSeq(tela.getAelExameAp().getSeq());
				listaTopografiaLaudos = null;
				listaDiagnosticoLaudos = null;
			}else {
				// Nova consulta topografia
				this.obterListaTopografiaLaudos();
				
				// consulta diagnóstico
				this.obterListaDiagnosticoLaudos();
				
				listaTopografiaAp = null;
				listaNomenclaturaAp = null;
			}
			
			// consulta patologista
			this.obterListaPatologistaLaudo();
		}
		if (cidOSeq != null) {
			aelTopografiaCidOs = examesPatologiaFacade.obterCidOPorChavePrimaria(cidOSeq);
			adicionarTopografiaLaudos();
		}

		if (seqCid != null) {
			aghCid = aghuFacade.obterAghCidPorSeq(seqCid);
			adicionarDiagnosticoLaudos();
		}
	}
	
	private void obterListaPatologistaLaudo(){
		listaPatologistaLaudoVO = examesPatologiaFacade.listarPatologistaLaudo(tela.getAelExameAp().getSeq());
		
		setPatologistaDataTableHeight(laudoUnicoController.dataTableSize(listaPatologistaLaudoVO));
	}
	
	private void obterListaTopografiaLaudos(){
		listaTopografiaLaudos = examesPatologiaFacade.listarTopografiaLaudosPorSeqExame(tela.getAelExameAp().getSeq());
		
		setTopografiaDataTableHeight(laudoUnicoController.dataTableSize(listaTopografiaLaudos));		
	}
	
	private void obterListaDiagnosticoLaudos(){
		listaDiagnosticoLaudos = examesPatologiaFacade.listarDiagnosticoLaudosPorSeqExame(tela.getAelExameAp().getSeq());
		
		setDiagnosticoDataTableHeight(laudoUnicoController.dataTableSize(listaDiagnosticoLaudos));	
	}

	public void limpar() {
		aelTopografiaCidOs = null;
		aelPatologista = null;
		aghCid = null;
		aelCidO = null;
	}

	public void adicionarTopografiaLaudos() {
		if (aelTopografiaCidOs != null) {
			AelTopografiaLaudos aelTopografiaLaudos = new AelTopografiaLaudos();
			aelTopografiaLaudos.setExameAp(tela.getAelExameAp());
			aelTopografiaLaudos.setTopografiaCidOs(aelTopografiaCidOs);
			try {
				examesPatologiaFacade.persistirTopografiaLaudos(aelTopografiaLaudos);
				aelTopografiaCidOs = null;
				cidOSeq = null;
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_INSERT_TOPOGRAFIA_AP");
			} catch (BaseException e) {
				cidOSeq = null;
				apresentarExcecaoNegocio(e);
			}
			
			this.obterListaTopografiaLaudos();
			
		} else {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_TOPOGRAFIA_CIDO_OBRIGATORIO");
		}
	}

	// habilita botao adicionar se etapas_laudo for diferente de LA 'laudo assinado'
	public Boolean habilitarBotaoAdicionar() {
		AelExameAp exameAp = examesPatologiaFacade.obterAelExameApPorAelAnatomoPatologicos(tela.getAelAnatomoPatologico());
		if (exameAp != null) {
			if (!exameAp.getEtapasLaudo().equals(DominioSituacaoExamePatologia.LA)) {
				return false;
			}
		}
		return true;
	}

	public void excluirTopografiaLaudos() {
		AelTopografiaLaudos aelTopografiaLaudos = examesPatologiaFacade
				.obterAelTopografiaLaudosPorChavePrimaria(seqExcluirTopografiaLaudoCidOs);
		try {
			examesPatologiaFacade.excluirTopografiaLaudos(aelTopografiaLaudos);
			this.obterListaTopografiaLaudos();
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_DELETE_TOPOGRAFIA_CIDOS");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void adicionarDiagnosticoLaudos() {
		if (aghCid != null || aelCidO != null) {
			AelDiagnosticoLaudos aelDiagnosticoLaudosCid = new AelDiagnosticoLaudos();
			AelDiagnosticoLaudos aelDiagnosticoLaudosCidO = new AelDiagnosticoLaudos();

			try {
				if (aghCid != null) {
					aelDiagnosticoLaudosCid.setExameAp(tela.getAelExameAp());
					aelDiagnosticoLaudosCid.setAghCid(aghCid);
					aelDiagnosticoLaudosCid.setAelCidos(null);
					examesPatologiaFacade.persistirDiagnosticoLaudos(aelDiagnosticoLaudosCid);
				}

				if (aelCidO != null) {
					aelDiagnosticoLaudosCidO.setExameAp(tela.getAelExameAp());
					aelDiagnosticoLaudosCidO.setAghCid(null);
					aelDiagnosticoLaudosCidO.setAelCidos(aelCidO);
					examesPatologiaFacade.persistirDiagnosticoLaudos(aelDiagnosticoLaudosCidO);
				}

				aghCid = null;
				aelCidO = null;
				seqCid = null;
				aelDiagnosticoLaudosCid = null;
				aelDiagnosticoLaudosCidO = null;

				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_INSERT_DIAGNOSTICO_LAUDOS");
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
			
			this.obterListaDiagnosticoLaudos();
			
		} else {
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_DIAGNOSTICO_NAO_INFORMADO");
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CODIGO_CID10_CIDO_DEVE_SER_INFORMADO");
		}
	}

	public void excluirDiagnosticoLaudos() {
		AelDiagnosticoLaudos aelDiagnosticoLaudos = examesPatologiaFacade
				.obterAelDiagnosticoLaudosPorChavePrimaria(seqExcluirDiagnosticoLaudos);
		try {
			examesPatologiaFacade.excluirDiagnosticoLaudos(aelDiagnosticoLaudos);
			this.obterListaDiagnosticoLaudos();
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_DELETE_DIAGNOSTICO_LAUDOS");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void adicionarPatologistaAp() {
		AelPatologistaAps patologistaAp = new AelPatologistaAps();
		patologistaAp.setAelExameAps(tela.getAelExameAp());
		patologistaAp.setServidor(aelPatologista.getServidor());
		patologistaAp.setOrdemMedicoLaudo((short) (listaPatologistaLaudoVO.size() + 1));

		try {

			examesPatologiaFacade.persistirAelPatologistaAps(patologistaAp);
			aelPatologista = null;
			this.obterListaPatologistaLaudo();
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_INSERT_PATOLOGISTA_AP");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	public void atualizarPatologistaAp(AelPatologistaAps patologistaAp) {
		try {

			examesPatologiaFacade.persistirAelPatologistaAps(patologistaAp);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	public void excluirPatologistaAp() {
		try {
			AelPatologistaAps aelPatologistaAps = examesPatologiaFacade.obterAelPatologistaApsPorChavePrimaria(seqExcluirPatologistaAp);

			RapServidores servidorPatologista = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(),
					new Date());

			examesPatologiaFacade.validarPatologistaExcluir(servidorPatologista, aelPatologistaAps.getServidor());

			Short ordem = aelPatologistaAps.getOrdemMedicoLaudo();

			examesPatologiaFacade.excluir(aelPatologistaAps);

			List<AelPatologistaAps> listaPatologistaAp = examesPatologiaFacade.listarPatologistaLaudoPorLuxSeqEOrdemMaior(tela
					.getAelExameAp().getSeq(), ordem);
			for (AelPatologistaAps item : listaPatologistaAp) {
				item.setOrdemMedicoLaudo((short) (item.getOrdemMedicoLaudo() - 1));

				atualizarPatologistaAp(item);
			}

			this.obterListaPatologistaLaudo();
			
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_DELETE_PATOLOGISTA_AP");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void excluir() {
		if (tipoExclusao.equals(PANEL_LISTA_DIAGNOSTICO_LAUDOS)) {
			excluirDiagnosticoLaudos();
		} else if (tipoExclusao.equals(PANEL_LISTA_PATOLOGISTA_AP)) {
			excluirPatologistaAp();
		} else if (tipoExclusao.equals(PANEL_LISTA_TOPOGRAFIA_LAUDO)) {
			excluirTopografiaLaudos();
		}
	}

	public void upPatologistaAp(AelPatologistaAps patologista) {
		List<AelPatologistaAps> listaAnterior = examesPatologiaFacade.listarPatologistaLaudoPorLuxSeqEOrdem(tela.getAelExameAp().getSeq(),
				(short) (patologista.getOrdemMedicoLaudo() - 1));
		AelPatologistaAps patologistaAnterior = listaAnterior.get(0);
		patologistaAnterior.setOrdemMedicoLaudo((short) (patologistaAnterior.getOrdemMedicoLaudo() + 1));
		atualizarPatologistaAp(patologistaAnterior);

		patologista.setOrdemMedicoLaudo((short) (patologista.getOrdemMedicoLaudo() - 1));
		atualizarPatologistaAp(patologista);

		this.obterListaPatologistaLaudo();
	}

	public void downPatologistaAp(AelPatologistaAps patologista) {
		List<AelPatologistaAps> listaAnterior = examesPatologiaFacade.listarPatologistaLaudoPorLuxSeqEOrdem(tela.getAelExameAp().getSeq(),
				(short) (patologista.getOrdemMedicoLaudo() + 1));
		AelPatologistaAps patologistaAnterior = listaAnterior.get(0);
		patologistaAnterior.setOrdemMedicoLaudo((short) (patologistaAnterior.getOrdemMedicoLaudo() - 1));
		atualizarPatologistaAp(patologistaAnterior);

		patologista.setOrdemMedicoLaudo((short) (patologista.getOrdemMedicoLaudo() + 1));
		atualizarPatologistaAp(patologista);

		this.obterListaPatologistaLaudo();
	}

	public List<AelTopografiaCidOs> pesquisarCidOs(String param) {
		return this.returnSGWithCount(examesPatologiaFacade.listarTopografiaCidOs(param),pesquisarCidOsCount(param));
	}

	public Long pesquisarCidOsCount(String param) {
		return examesPatologiaFacade.listarTopografiaCidOsCount(param);
	}

	public List<AelCidos> pesquisarAelCidos(String param) {
		return this.returnSGWithCount(examesPatologiaFacade.listarAelCidos(param),pesquisarAelCidosCount(param));
	}

	public Long pesquisarAelCidosCount(String param) {
		return examesPatologiaFacade.listarAelCidosCount(param);
	}

	public String pesquisarCidOsPorCapitulo() {
		return "pesquisaTopografiaCidO";
	}

	public String pesquisarCid10PorCapitulo() {
		return "internacao-pesquisaCid";
	}

	public List<AghCid> pesquisarCids(String param) {
		return this.returnSGWithCount(aghuFacade.pesquisarCidPorCodigoDescricao(100, param),pesquisarCidsCount(param));
	}

	public Long pesquisarCidsCount(String param) {
		return aghuFacade.pesquisarCidPorCodigoDescricaoCount(param);
	}

	public List<AelPatologista> pesquisarPatologistaResponsavel(String filtro) {
		return this.returnSGWithCount(examesPatologiaFacade.listarPatologistasAtivosPorCodigoNomeFuncao((String) filtro, DominioFuncaoPatologista.P,
				DominioFuncaoPatologista.C, DominioFuncaoPatologista.R),pesquisarPatologistaResponsavelCount(filtro));
	}

	public Integer pesquisarPatologistaResponsavelCount(String filtro) {
		return examesPatologiaFacade.listarPatologistasAtivosPorCodigoNomeFuncaoCount((String) filtro, DominioFuncaoPatologista.P,
				DominioFuncaoPatologista.C, DominioFuncaoPatologista.R).intValue();
	}

	public AelPatologista getAelPatologista() {
		return aelPatologista;
	}

	public void setAelPatologista(AelPatologista aelPatologista) {
		this.aelPatologista = aelPatologista;
	}

	public Integer getSeqExcluirPatologistaAp() {
		return seqExcluirPatologistaAp;
	}

	public void setSeqExcluirPatologistaAp(Integer seqExcluirPatologistaAp) {
		this.seqExcluirPatologistaAp = seqExcluirPatologistaAp;
	}

	public List<AelPatologistaLaudoVO> getListaPatologistaLaudoVO() {
		return listaPatologistaLaudoVO;
	}

	public void setListaPatologistaLaudoVO(List<AelPatologistaLaudoVO> listaPatologistaLaudoVO) {
		this.listaPatologistaLaudoVO = listaPatologistaLaudoVO;
	}

	public String getTipoExclusao() {
		return tipoExclusao;
	}

	public void setTipoExclusao(String tipoExclusao) {
		this.tipoExclusao = tipoExclusao;
	}

	public TelaLaudoUnicoVO getTela() {
		return tela;
	}

	public void setTela(TelaLaudoUnicoVO tela) {
		this.tela = tela;
	}

	public AelTopografiaCidOs getAelTopografiaCidOs() {
		return aelTopografiaCidOs;
	}

	public void setAelTopografiaCidOs(AelTopografiaCidOs aelTopografiaCidOs) {
		this.aelTopografiaCidOs = aelTopografiaCidOs;
	}

	public List<AelDiagnosticoLaudos> getListaDiagnosticoLaudos() {
		return listaDiagnosticoLaudos;
	}

	public void setListaDiagnosticoLaudos(List<AelDiagnosticoLaudos> listaDiagnosticoLaudos) {
		this.listaDiagnosticoLaudos = listaDiagnosticoLaudos;
	}

	public List<AelTopografiaLaudos> getListaTopografiaLaudos() {
		return listaTopografiaLaudos;
	}

	public AelDiagnosticoLaudos getAelDiagnosticoLaudos() {
		return aelDiagnosticoLaudos;
	}

	public void setAelDiagnosticoLaudos(AelDiagnosticoLaudos aelDiagnosticoLaudos) {
		this.aelDiagnosticoLaudos = aelDiagnosticoLaudos;
	}

	public void setListaTopografiaLaudos(List<AelTopografiaLaudos> listaTopografiaLaudos) {
		this.listaTopografiaLaudos = listaTopografiaLaudos;
	}

	public Long getSeqExcluirTopografiaLaudoCidOs() {
		return seqExcluirTopografiaLaudoCidOs;
	}

	public void setSeqExcluirTopografiaLaudoCidOs(Long seqExcluirTopografiaLaudoCidOs) {
		this.seqExcluirTopografiaLaudoCidOs = seqExcluirTopografiaLaudoCidOs;
	}

	public Long getCidOSeq() {
		return cidOSeq;
	}

	public void setCidOSeq(Long cidOSeq) {
		this.cidOSeq = cidOSeq;
	}

	public AelCidos getAelCidO() {
		return aelCidO;
	}

	public void setAelCidO(AelCidos aelCidO) {
		this.aelCidO = aelCidO;
	}

	public AghCid getAghCid() {
		return aghCid;
	}

	public void setAghCid(AghCid aghCid) {
		this.aghCid = aghCid;
	}

	public Long getSeqExcluirDiagnosticoLaudos() {
		return seqExcluirDiagnosticoLaudos;
	}

	public void setSeqExcluirDiagnosticoLaudos(Long seqExcluirDiagnosticoLaudos) {
		this.seqExcluirDiagnosticoLaudos = seqExcluirDiagnosticoLaudos;
	}

	public Integer getSeqCid() {
		return seqCid;
	}

	public void setSeqCid(Integer seqCid) {
		this.seqCid = seqCid;
	}
	
	public List<AelTopografiaAps> getListaTopografiaAp() {
		return listaTopografiaAp;
	}

	public void setListaTopografiaAp(List<AelTopografiaAps> listaTopografiaAp) {
		this.listaTopografiaAp = listaTopografiaAp;
	}

	public List<AelNomenclaturaAps> getListaNomenclaturaAp() {
		return listaNomenclaturaAp;
	}

	public void setListaNomenclaturaAp(List<AelNomenclaturaAps> listaNomenclaturaAp) {
		this.listaNomenclaturaAp = listaNomenclaturaAp;
	}
	
	public boolean isLaudoAntigo() {
		return laudoAntigo;
	}

	public void setLaudoAntigo(boolean laudoAntigo) {
		this.laudoAntigo = laudoAntigo;
	}

	public Integer getTopografiaDataTableHeight() {
		return topografiaDataTableHeight;
	}

	public void setTopografiaDataTableHeight(Integer topografiaDataTableHeight) {
		this.topografiaDataTableHeight = topografiaDataTableHeight;
	}

	public Integer getDiagnosticoDataTableHeight() {
		return diagnosticoDataTableHeight;
	}

	public void setDiagnosticoDataTableHeight(Integer diagnosticoDataTableHeight) {
		this.diagnosticoDataTableHeight = diagnosticoDataTableHeight;
	}

	public Integer getPatologistaDataTableHeight() {
		return patologistaDataTableHeight;
	}
	
	public void setPatologistaDataTableHeight(Integer patologiaDataTableHeight) {
		this.patologistaDataTableHeight = patologiaDataTableHeight;
	}	
}