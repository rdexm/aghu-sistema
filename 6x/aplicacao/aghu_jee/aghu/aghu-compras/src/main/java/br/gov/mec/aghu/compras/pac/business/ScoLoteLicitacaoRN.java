package br.gov.mec.aghu.compras.pac.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.ScoItemLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoLoteLicitacaoDAO;
import br.gov.mec.aghu.compras.dao.ScoParecerAvaliacaoDAO;
import br.gov.mec.aghu.compras.vo.ItensPACVO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoLoteLicitacao;
import br.gov.mec.aghu.model.ScoLoteLicitacaoId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * RN 
 *  
 * @author rpanassolo
 */
@Stateless
public class ScoLoteLicitacaoRN extends BaseBusiness {
	private static final long serialVersionUID = -5180375676145620896L;
	
	@Inject
	private ScoLoteLicitacaoDAO scoLoteLicitacaoDAO;
	
	@Inject
	private ScoItemLicitacaoDAO scoItemLicitacaoDAO;
	
	@Inject
	private ScoParecerAvaliacaoDAO scoParecerAvaliacaoDAO;
	
	@Inject
	private	ScoLicitacaoDAO scoLicitacaoDAO;
	
	private static final Log LOG = LogFactory.getLog(ScoLoteLicitacaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}	

	public void persistir(ScoLoteLicitacao loteLicitacao)
			throws ApplicationBusinessException {
		if (loteLicitacao.getId() != null && loteLicitacao.getId().getNumero()==null) {
			loteLicitacao = antesInserir(loteLicitacao);
			getScoLoteLicitacaoDAO().persistir(loteLicitacao);
		} else {
			getScoLoteLicitacaoDAO().atualizar(loteLicitacao);
		}
	}

	public void remover(ScoLoteLicitacao loteLicitacao) {		
		getScoLoteLicitacaoDAO().remover(loteLicitacao);		
	}

	public ScoLoteLicitacao gerarLoteByItem(ItensPACVO itemVO){
		ScoLoteLicitacao lote = new  ScoLoteLicitacao();
		lote.setDescricao(itemVO.getNomeMaterial());
		ScoLicitacao scoLicitacao = getScoLicitacaoDAO().obterPorChavePrimaria(itemVO.getNumeroLicitacao());
		ScoLoteLicitacaoId id = new ScoLoteLicitacaoId();
		lote.setScoLicitacao(scoLicitacao);
		id.setLctNumero(itemVO.getNumeroLicitacao());
		id.setNumero(itemVO.getNumeroItem());
		lote.setId(id);
		getScoLoteLicitacaoDAO().persistir(lote);
		return lote;
	}
	
	private  ScoLoteLicitacao antesInserir(ScoLoteLicitacao loteLicitacao){
		ScoLoteLicitacaoId id = loteLicitacao.getId();
		Short numero = getScoLoteLicitacaoDAO().obterSequenceLoteLicitacao(id.getLctNumero());
		if(numero!=null){
			numero++;
		}else{
			numero = Short.valueOf("1");
		}
		id.setNumero(numero);
		loteLicitacao.setId(id);
		return loteLicitacao;
	}
	
	
	public String obterParecerTecnico(Integer codigo ){
		String parecer = getScoParecerAvaliacaoDAO().obterParecerTecnicoItem(codigo);
		return  parecer;
	}
	
	public String obterParecerTecnicoAtivo(Integer codigo ){
		String parecer = getScoParecerAvaliacaoDAO().obterParecerTecnicoItem(codigo, DominioSituacao.A);
		return  parecer;
	}
	
	
	public Boolean verificarDependenciasDoItem(Integer nroPac, Integer materialCod, Short nroLote ){
		List<ScoItemLicitacao> retorno = new ArrayList<ScoItemLicitacao>(); 
		retorno.addAll(getScoItemLicitacaoDAO().verificarDependenciasDoItemUnion1(nroPac, materialCod));
		retorno.addAll(getScoItemLicitacaoDAO().verificarDependenciasDoItemUnion2(nroPac, materialCod));
		if(retorno !=null && !retorno.isEmpty()){
			if(retorno.size()== 1 && nroLote!=null && nroLote>0){
				ScoItemLicitacao item = retorno.get(0);
				ScoLoteLicitacaoId id = new ScoLoteLicitacaoId(nroPac, nroLote);
				ScoLoteLicitacao lote = getScoLoteLicitacaoDAO().obterPorChavePrimaria(id);
				if(item.getLoteLicitacao()!=null && lote!=null){
					if(item.getLoteLicitacao().equals(lote)){
						return false;
					}
				}
			}
			return true;
		}
		return false;
	}
	
	protected ScoItemLicitacaoDAO getScoItemLicitacaoDAO() {
		return scoItemLicitacaoDAO;
	}
	
	private ScoLoteLicitacaoDAO getScoLoteLicitacaoDAO() {
		return scoLoteLicitacaoDAO;
	}
	
	private ScoParecerAvaliacaoDAO getScoParecerAvaliacaoDAO() {
		return scoParecerAvaliacaoDAO;
	}
	
	private ScoLicitacaoDAO getScoLicitacaoDAO(){
		return scoLicitacaoDAO;
	}

}
