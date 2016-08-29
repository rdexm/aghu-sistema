package br.gov.mec.aghu.blococirurgico.action.procdiagterap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.procdiagterap.business.IBlocoCirurgicoProcDiagTerapFacade;
import br.gov.mec.aghu.blococirurgico.vo.ComplementoCidVO;
import br.gov.mec.aghu.blococirurgico.vo.DescricaoProcDiagTerapVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.PdtCidDesc;
import br.gov.mec.aghu.model.PdtCidDescId;
import br.gov.mec.aghu.model.PdtComplementoPorCid;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioListarCirurgiasPdtDescProcCirurgiaController;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;


public class DescricaoProcDiagTerapResultadoController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -7430845672084292757L;
	
	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade;

	@EJB
	private IAghuFacade aghuFacade;

	private Integer ddtSeq;
	private Integer cidSeq;
	private Short procCidSeqp;
	private Boolean resultadoNormal;
	private ComplementoCidVO complementoCid;
	private PdtCidDesc cidDesc;
	private List<PdtCidDesc> listaCidDesc;
	private PdtComplementoPorCid complementoPorCid;
	private AghCid cid;
	private PdtComplementoPorCid complemento;
	private List<PdtComplementoPorCid> complementosCid;
	private String complementoLivre;
	private static final String PAGE_PESQUISA_CID = "internacao-pesquisaCid";
	private PdtDescricao descricao;
	private Map<PdtCidDescId, List<PdtComplementoPorCid>> mapComplementos = new HashMap<PdtCidDescId, List<PdtComplementoPorCid>>();
	
	@Inject
	private RelatorioListarCirurgiasPdtDescProcCirurgiaController relatorioListarCirurgiasPdtDescProcCirurgiaController;
	
	public void iniciar(DescricaoProcDiagTerapVO descricaoProcDiagTerapVO, PdtDescricao descricao) {
		this.descricao = descricao;
		ddtSeq = descricaoProcDiagTerapVO.getDdtSeq();
		this.listaCidDesc = blocoCirurgicoProcDiagTerapFacade.pesquisarPdtCidDescPorDdtSeqComCidAtivo(ddtSeq);
		for(PdtCidDesc descricaoCid: listaCidDesc){
			this.complementosCid = blocoCirurgicoProcDiagTerapFacade.obterListaComplementoCidAtivos(ddtSeq, descricaoCid.getAghCid().getSeq());
			mapComplementos.put(descricaoCid.getId(), complementosCid);
		}
		
		
		if(cidSeq != null) {
			this.cid = this.aghuFacade.obterAghCidPorChavePrimaria(cidSeq);
		}
		this.resultadoNormal = descricao.getResultadoNormal();
	}
	
	public void limpar() {
		cidSeq = null;
		resultadoNormal = false;
		complementoCid = null;
		cidDesc = null;
		complementoPorCid = null;
		cid=null;
		complemento=null;
		complementoLivre=null;
	}
	
	public String pesquisaCidCapitulo(){
		return PAGE_PESQUISA_CID;
	}
	
	public List<ComplementoCidVO> pesquisarCidProc(String strPesquisa) {
		return this.returnSGWithCount(blocoCirurgicoProcDiagTerapFacade.obterListaComplementoCid(ddtSeq, strPesquisa), 
				blocoCirurgicoProcDiagTerapFacade.obterListaComplementoCidCount(ddtSeq, strPesquisa));
	}

	public List<AghCid> pesquisarCid(String strPesquisa) {
		return this.returnSGWithCount(aghuFacade.listarAghCidPorCodigoDescricao(strPesquisa , null, DominioSituacao.A, false, false, null, null, true), 
				aghuFacade.listarAghCidPorCodigoDescricaoCount(strPesquisa, null, DominioSituacao.A, false, false));
	}

	public void posSelectionCidProc() {
		this.cid = this.aghuFacade.obterAghCidPorChavePrimaria(complementoCid.getSeq());
		this.complementoPorCid = null;
		this.gravar();
	}
	
	public void posDeleteCidProc() {
		this.cid = null;
		this.complementoPorCid = null;
	}
	
	
	public void atualizarComplemento(PdtCidDesc item){
		try {
			blocoCirurgicoProcDiagTerapFacade.persistirPdtCidDesc(item);
			relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void atualizarComplemento(PdtCidDesc item, PdtComplementoPorCid complemento){
		try {
			item.setPdtComplementoPorCid(complemento);
			blocoCirurgicoProcDiagTerapFacade.persistirPdtCidDesc(item);
			relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void alterarResultadoNormal() {
		if(Boolean.TRUE.equals(resultadoNormal)){
			cidSeq = null;
			complementoCid = null;
			cidDesc = null;
			complementoPorCid = null;
			cid=null;
			complemento=null;
			complementoLivre=null;
		}
		try {
			this.descricao.setResultadoNormal(resultadoNormal);
			blocoCirurgicoProcDiagTerapFacade.atualizarDescricao(descricao);
			relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void excluir() {
		try {
			blocoCirurgicoProcDiagTerapFacade.excluirPdtCidDesc(blocoCirurgicoProcDiagTerapFacade.obterPdtCidDesc(new PdtCidDescId(ddtSeq, procCidSeqp)));
			mapComplementos.remove(new PdtCidDescId(ddtSeq, procCidSeqp));
			this.listaCidDesc = blocoCirurgicoProcDiagTerapFacade.pesquisarPdtCidDescPorDdtSeqComCidAtivo(ddtSeq);
			relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void gravar() {
		try {
			if(complementoCid!=null){
				this.complementosCid = blocoCirurgicoProcDiagTerapFacade.obterListaComplementoCidAtivos(ddtSeq, complementoCid.getSeq());
			} else if(cid!=null){
				this.complementosCid = blocoCirurgicoProcDiagTerapFacade.obterListaComplementoCidAtivos(ddtSeq, cid.getSeq());
			}
			
			
			this.blocoCirurgicoProcDiagTerapFacade.validarResultadoNormalOuCid(resultadoNormal, cid);

			this.cidDesc = new PdtCidDesc();
			this.cidDesc.setId(new PdtCidDescId(ddtSeq, null));
			this.cidDesc.setAghCid(cid);
			this.cidDesc.setComplementoLivre(complementoLivre);
			if(complementoPorCid != null) {
				this.cidDesc.setPdtComplementoPorCid(complementoPorCid);
			}
			blocoCirurgicoProcDiagTerapFacade.persistirPdtCidDesc(this.cidDesc);
			mapComplementos.put(this.getCidDesc().getId(), complementosCid);
			this.limpar();
			this.listaCidDesc = blocoCirurgicoProcDiagTerapFacade.pesquisarPdtCidDescPorDdtSeqComCidAtivo(ddtSeq);
			relatorioListarCirurgiasPdtDescProcCirurgiaController.inicio();
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public Integer getDdtSeq() {
		return ddtSeq;
	}

	public void setDdtSeq(Integer ddtSeq) {
		this.ddtSeq = ddtSeq;
	}

	public Boolean getResultadoNormal() {
		return resultadoNormal;
	}

	public void setResultadoNormal(Boolean resultadoNormal) {
		this.resultadoNormal = resultadoNormal;
	}

	public ComplementoCidVO getComplementoCid() {
		return complementoCid;
	}

	public void setComplementoCid(ComplementoCidVO complementoCid) {
		this.complementoCid = complementoCid;
	}

	public PdtComplementoPorCid getComplementoPorCid() {
		return complementoPorCid;
	}

	public void setComplementoPorCid(PdtComplementoPorCid complementoPorCid) {
		this.complementoPorCid = complementoPorCid;
	}

	public AghCid getCid() {
		return cid;
	}

	public void setCid(AghCid cid) {
		this.cid = cid;
	}

	public PdtComplementoPorCid getComplemento() {
		return complemento;
	}

	public void setComplemento(PdtComplementoPorCid complemento) {
		this.complemento = complemento;
	}

	public List<PdtComplementoPorCid> getComplementosCid() {
		return complementosCid;
	}

	public void setComplementosCid(List<PdtComplementoPorCid> complementosCid) {
		this.complementosCid = complementosCid;
	}

	public PdtCidDesc getCidDesc() {
		return cidDesc;
	}

	public void setCidDesc(PdtCidDesc cidDesc) {
		this.cidDesc = cidDesc;
	}

	public List<PdtCidDesc> getListaCidDesc() {
		return listaCidDesc;
	}

	public void setListaCidDesc(List<PdtCidDesc> listaCidDesc) {
		this.listaCidDesc = listaCidDesc;
	}

	public String getComplementoLivre() {
		return complementoLivre;
	}

	public void setComplementoLivre(String complementoLivre) {
		this.complementoLivre = complementoLivre;
	}

	public Integer getCidSeq() {
		return cidSeq;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}

	public Short getProcCidSeqp() {
		return procCidSeqp;
	}

	public void setProcCidSeqp(Short procCidSeqp) {
		this.procCidSeqp = procCidSeqp;
	}

	protected PdtDescricao getDescricao() {
		return descricao;
	}

	protected void setDescricao(PdtDescricao descricao) {
		this.descricao = descricao;
	}

	public Map<PdtCidDescId, List<PdtComplementoPorCid>> getMapComplementos() {
		return mapComplementos;
	}

	public void setMapComplementos(
			Map<PdtCidDescId, List<PdtComplementoPorCid>> mapComplementos) {
		this.mapComplementos = mapComplementos;
	}
	
	
}
