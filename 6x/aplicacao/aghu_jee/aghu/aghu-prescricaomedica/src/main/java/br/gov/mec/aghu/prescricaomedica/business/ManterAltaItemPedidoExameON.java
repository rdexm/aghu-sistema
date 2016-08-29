package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AelMateriaisAnalises;
import br.gov.mec.aghu.model.AelUnfExecutaExames;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmAltaItemPedidoExame;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.VAelExamesSolicitacao;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaItemPedidoExameDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author lalegre
 *r
 */
@Stateless
public class ManterAltaItemPedidoExameON extends BaseBusiness {


@EJB
private ManterAltaItemPedidoExameRN manterAltaItemPedidoExameRN;

private static final Log LOG = LogFactory.getLog(ManterAltaItemPedidoExameON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaItemPedidoExameDAO mpmAltaItemPedidoExameDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1487829006648490184L;

	/**
	 * Atualiza MpmAltaItemPedidoExame do sumário ativo
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 * @param novoSeqp
	 * @throws ApplicationBusinessException
	 */
	public void versionarAltaItemPedidoExame(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws ApplicationBusinessException {
		
		List<MpmAltaItemPedidoExame> itens = this.getMpmAltaItemPedidoExameDAO().obterMpmAltaItemPedidoExame(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), antigoAsuSeqp);
		
		for (MpmAltaItemPedidoExame altaItemPedidoExame : itens) {
			
			MpmAltaItemPedidoExame novoAltaItemPedidoExame = new MpmAltaItemPedidoExame();
			novoAltaItemPedidoExame.setMpmAltaSumarios(altaSumario);
			novoAltaItemPedidoExame.setAelUnfExecutaExames(altaItemPedidoExame.getAelUnfExecutaExames());
			novoAltaItemPedidoExame.setDescExame(altaItemPedidoExame.getDescExame());
			novoAltaItemPedidoExame.setMpmAltaPedidoExames(altaItemPedidoExame.getMpmAltaPedidoExames());
			this.getManterAltaItemPedidoExameRN().inserirAltaItemPedidoExame(novoAltaItemPedidoExame);
			
		}
		
	}
	
	/**
	 * Remove MpmAltaItemPedidoExame do sumário ativo
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 * @param novoSeqp
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaItemPedidoExame(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		
		List<MpmAltaItemPedidoExame> itens = this.getMpmAltaItemPedidoExameDAO().obterMpmAltaItemPedidoExame(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp());
		
		for (MpmAltaItemPedidoExame altaItemPedidoExame : itens) {
			
			this.getManterAltaItemPedidoExameRN().removerAltaItemPedidoExame(altaItemPedidoExame);
			
		}
		
	}
	
	

	public List<MpmAltaItemPedidoExame> obterMpmAltaItemPedidoExame(
			Integer altanAtdSeq, Integer altanApaSeq, Short altanAsuSeqp) throws ApplicationBusinessException {
		return this.getMpmAltaItemPedidoExameDAO().obterMpmAltaItemPedidoExame(altanAtdSeq, altanApaSeq, altanAsuSeqp);
	}

	public AelUnfExecutaExames buscarAelUnfExecutaExamesPorID(Integer manSeq,
			String sigla, Integer unfSeq) {
		return this.getMpmAltaItemPedidoExameDAO().buscarAelUnfExecutaExamesPorID(manSeq,sigla,unfSeq);
	}
	

	public void inserirAltaItemPedidoExame(MpmAltaItemPedidoExame item) throws ApplicationBusinessException {
		this.getManterAltaItemPedidoExameRN().inserirAltaItemPedidoExame(item);
	}

	public List<VAelExamesSolicitacao> obterNomeExames(Object objPesquisa) {
		return this.getMpmAltaItemPedidoExameDAO().obterNomeExames(objPesquisa);
	}
	
	public Long obterNomeExamesCount(Object objPesquisa) {
		return this.getMpmAltaItemPedidoExameDAO().obterNomeExamesCount(objPesquisa);
	}
	
	public AelMateriaisAnalises buscarAelMateriaisAnalisesPorAelUnfExecutaExames(AelUnfExecutaExames aelUnfExecutaExames){
		return this.getMpmAltaItemPedidoExameDAO().buscarAelMateriaisAnalisesPorAelUnfExecutaExames(aelUnfExecutaExames);
	}

	public AghUnidadesFuncionais buscarAghUnidadesFuncionaisPorAelUnfExecutaExames(
			AelUnfExecutaExames aelUnfExecutaExames) {
		return this.getMpmAltaItemPedidoExameDAO().buscarAghUnidadesFuncionaisPorAelUnfExecutaExames(aelUnfExecutaExames);
	}

	public void excluirMpmAltaItemPedidoExame(
			MpmAltaItemPedidoExame altaItemPedidoExame) throws ApplicationBusinessException {
		
		MpmAltaItemPedidoExame altaItem = this.getMpmAltaItemPedidoExameDAO().obterPorChavePrimaria(altaItemPedidoExame.getId());
		
		if (altaItem != null) {
			this.getMpmAltaItemPedidoExameDAO().remover(altaItem);
			this.getMpmAltaItemPedidoExameDAO().flush();
		}
	}

	protected MpmAltaItemPedidoExameDAO getMpmAltaItemPedidoExameDAO() {
		return mpmAltaItemPedidoExameDAO;
	}
	
	protected ManterAltaItemPedidoExameRN getManterAltaItemPedidoExameRN() {
		return manterAltaItemPedidoExameRN;
	}
	
}
