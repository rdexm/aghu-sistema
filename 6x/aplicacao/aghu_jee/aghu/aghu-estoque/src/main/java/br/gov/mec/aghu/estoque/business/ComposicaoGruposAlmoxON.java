package br.gov.mec.aghu.estoque.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoComposicaoDAO;
import br.gov.mec.aghu.estoque.dao.SceAlmoxarifadoGruposDAO;
import br.gov.mec.aghu.estoque.vo.ComposicaoGruposVO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceAlmoxarifadoComposicao;
import br.gov.mec.aghu.model.SceAlmoxarifadoGrupos;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class ComposicaoGruposAlmoxON extends BaseBusiness {
	
	private static final long serialVersionUID = 5959174924562412131L;

	private static final Log LOG = LogFactory.getLog(ComposicaoGruposAlmoxON.class);
	
	@Inject
	private SceAlmoxarifadoComposicaoDAO sceAlmoxarifadoComposicaoDAO;
	
	@Inject
	private SceAlmoxarifadoGruposDAO sceAlmoxarifadoGruposDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	
	public List<ComposicaoGruposVO> pesquisarComposicaoAlmox(SceAlmoxarifado almoxarifado) {
		List<ComposicaoGruposVO> listaVO = new ArrayList<ComposicaoGruposVO>();
		
		List<SceAlmoxarifadoComposicao> listaComp = getSceAlmoxarifadoComposicaoDAO().
				pesquisarPorAlmoxarifado(almoxarifado);
		
		if (listaComp != null && !listaComp.isEmpty()) {
			for (SceAlmoxarifadoComposicao comp : listaComp) {
				
				ComposicaoGruposVO vo = new ComposicaoGruposVO();
				
				comp.setServidorInclusao(this.registroColaboradorFacade.obterServidor(comp.getServidorInclusao().getId()));
				
				if (comp.getServidorInclusao() != null && comp.getServidorInclusao().getPessoaFisica() != null){
					try {
						comp.getServidorInclusao().setPessoaFisica(this.registroColaboradorFacade.obterPessoaFisica(comp.getServidorInclusao().getPessoaFisica().getCodigo()));
					} catch (ApplicationBusinessException e) {
						LOG.error("ERRO AO OBTER PESSOA FISICA" );
					}
				}
				
				vo.setComposicao(comp);
				vo.setDescricaoComposicao(comp.getDescricao());
				vo.setServidorInclusao(comp.getServidorInclusao());
				vo.setSeq(comp.getSeq());
				vo.setIdRow(comp.getSeq().shortValue());
				
				List<SceAlmoxarifadoGrupos> listaGrupos = getSceAlmoxarifadoGruposDAO().pesquisarGruposPorComposicao(comp);
				
				if (listaGrupos != null && !listaGrupos.isEmpty()) {
					List<ScoGrupoMaterial> lGrup = new ArrayList<ScoGrupoMaterial>();
					for (SceAlmoxarifadoGrupos grp : listaGrupos) {
						lGrup.add(this.comprasFacade.obterGrupoMaterialPorId(grp.getGrupoMaterial().getCodigo()));
					}
					vo.setListaGrupos(lGrup);
				} else {
					vo.setListaGrupos(new ArrayList<ScoGrupoMaterial>());
				}
				
				listaVO.add(vo);
			}
		}
		return listaVO;
	}
	
	private void removerListaAntiga(List<SceAlmoxarifadoComposicao> listaAntiga) {
		if (listaAntiga != null && !listaAntiga.isEmpty()) {
			for (SceAlmoxarifadoComposicao comp : listaAntiga) {
				List<SceAlmoxarifadoGrupos> listaGrupos = this.getSceAlmoxarifadoGruposDAO().pesquisarGruposPorComposicao(comp);
				
				if (listaGrupos != null && !listaGrupos.isEmpty()) {
					for (SceAlmoxarifadoGrupos grp : listaGrupos) {
						SceAlmoxarifadoGrupos grpExclusao = this.getSceAlmoxarifadoGruposDAO().obterPorChavePrimaria(grp.getSeq());
						if (grpExclusao != null) {
							this.getSceAlmoxarifadoGruposDAO().remover(grpExclusao);
						}
					}
				}
				SceAlmoxarifadoComposicao compExclusao = this.getSceAlmoxarifadoComposicaoDAO().obterPorChavePrimaria(comp.getSeq());
				if (compExclusao != null) {
					this.getSceAlmoxarifadoComposicaoDAO().remover(compExclusao);
				}
			}
		}
		
		this.getSceAlmoxarifadoComposicaoDAO().flush();
	}

	public Boolean verificarGrupoOutraComposicao(ScoGrupoMaterial grupo, Integer seqComposicao, SceAlmoxarifado almox, List<ComposicaoGruposVO> listaTela) {
		Boolean ret = Boolean.FALSE;
		
		for (ComposicaoGruposVO comp : listaTela) {
			if (comp.getSeq() != null) {
				continue;
			}
			for (ScoGrupoMaterial grp : comp.getListaGrupos()) {
				if (grp.equals(grupo)) {
					ret = Boolean.TRUE;
					break;
				}
			}
		}
		
		if (!ret && almox != null && almox.getSeq() != null) {
			ret = this.getSceAlmoxarifadoGruposDAO().verificarGrupoOutraComposicao(grupo, seqComposicao, almox);
		}
		
		return ret;
	}
	
	public void removerComposicaoAlmox(SceAlmoxarifado almoxarifado) {
		this.removerListaAntiga(this.getSceAlmoxarifadoComposicaoDAO().pesquisarPorAlmoxarifado(almoxarifado));
	}
	
	public void persistirComposicaoAlmox(RapServidores servidorLogado, SceAlmoxarifado almoxarifado, List<ComposicaoGruposVO> listaVO) throws ApplicationBusinessException {
		this.removerListaAntiga(this.getSceAlmoxarifadoComposicaoDAO().pesquisarPorAlmoxarifado(almoxarifado)); 
		
		for (ComposicaoGruposVO comp : listaVO) {
			SceAlmoxarifadoComposicao novaComposicao = new SceAlmoxarifadoComposicao();
			novaComposicao.setAlmoxarifado(almoxarifado);
			novaComposicao.setDescricao(comp.getDescricaoComposicao());
			novaComposicao.setServidorInclusao(servidorLogado);
			
			this.getSceAlmoxarifadoComposicaoDAO().persistir(novaComposicao);
			
			for (ScoGrupoMaterial grp : comp.getListaGrupos()) {
				SceAlmoxarifadoGrupos novoGrupo = new SceAlmoxarifadoGrupos();
				novoGrupo.setComposicao(novaComposicao);
				novoGrupo.setGrupoMaterial(grp);
				
				this.getSceAlmoxarifadoGruposDAO().persistir(novoGrupo);
			}
		}
	}
	
	public void persistirComposicaoAlmox(SceAlmoxarifado almoxarifado, List<ComposicaoGruposVO> listaVO) throws ApplicationBusinessException {
		this.removerListaAntiga(this.getSceAlmoxarifadoComposicaoDAO().pesquisarPorAlmoxarifado(almoxarifado)); 
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		for (ComposicaoGruposVO comp : listaVO) {
			SceAlmoxarifadoComposicao novaComposicao = new SceAlmoxarifadoComposicao();
			novaComposicao.setAlmoxarifado(almoxarifado);
			novaComposicao.setDescricao(comp.getDescricaoComposicao());
			novaComposicao.setServidorInclusao(servidorLogado);
			
			this.getSceAlmoxarifadoComposicaoDAO().persistir(novaComposicao);
			
			for (ScoGrupoMaterial grp : comp.getListaGrupos()) {
				SceAlmoxarifadoGrupos novoGrupo = new SceAlmoxarifadoGrupos();
				novoGrupo.setComposicao(novaComposicao);
				novoGrupo.setGrupoMaterial(grp);
				
				this.getSceAlmoxarifadoGruposDAO().persistir(novoGrupo);
			}
		}
	}
	
	
	protected SceAlmoxarifadoComposicaoDAO getSceAlmoxarifadoComposicaoDAO() {
		return sceAlmoxarifadoComposicaoDAO;
	}

	protected void setSceAlmoxarifadoComposicaoDAO(SceAlmoxarifadoComposicaoDAO sceAlmoxarifadoComposicaoDAO) {
		this.sceAlmoxarifadoComposicaoDAO = sceAlmoxarifadoComposicaoDAO;
	}

	protected SceAlmoxarifadoGruposDAO getSceAlmoxarifadoGruposDAO() {
		return sceAlmoxarifadoGruposDAO;
	}

	protected void setSceAlmoxarifadoGruposDAO(SceAlmoxarifadoGruposDAO sceAlmoxarifadoGruposDAO) {
		this.sceAlmoxarifadoGruposDAO = sceAlmoxarifadoGruposDAO;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}
}
