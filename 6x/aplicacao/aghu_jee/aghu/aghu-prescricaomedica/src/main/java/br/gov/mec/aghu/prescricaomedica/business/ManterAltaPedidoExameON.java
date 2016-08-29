package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaPedidoExame;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaPedidoExameDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterAltaPedidoExameON extends BaseBusiness {


@EJB
private ManterAltaPedidoExameRN manterAltaPedidoExameRN;

private static final Log LOG = LogFactory.getLog(ManterAltaPedidoExameON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaPedidoExameDAO mpmAltaPedidoExameDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5634320078918167768L;

	/**
	 * Atualiza alta pedido exame do sumário ativo
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 * @throws ApplicationBusinessException
	 */
	public void versionarAltaPedidoExame(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws ApplicationBusinessException {
		
		MpmAltaPedidoExame altaPedidoExame = this.getMpmAltaPedidoExameDAO().obterMpmAltaPedidoExame(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), antigoAsuSeqp);
		
		if (altaPedidoExame != null) {
			
			MpmAltaPedidoExame novoAltaPedidoExame = new MpmAltaPedidoExame();
			novoAltaPedidoExame.setAltaSumario(altaSumario);
			novoAltaPedidoExame.setAacUnidFuncionalSalas(altaPedidoExame.getAacUnidFuncionalSalas());
			novoAltaPedidoExame.setAghEquipes(altaPedidoExame.getAghEquipes());
			novoAltaPedidoExame.setAghEspecialidades(altaPedidoExame.getAghEspecialidades());
			novoAltaPedidoExame.setAghProfEspecialidades(altaPedidoExame.getAghProfEspecialidades());
			novoAltaPedidoExame.setAghUnidadesFuncionais(altaPedidoExame.getAghUnidadesFuncionais());
			novoAltaPedidoExame.setDescConvenio(altaPedidoExame.getDescConvenio());
			novoAltaPedidoExame.setDescEquipe(altaPedidoExame.getDescEquipe());
			novoAltaPedidoExame.setDescEspecialidade(altaPedidoExame.getDescEspecialidade());
			novoAltaPedidoExame.setDescPlanoConvenio(altaPedidoExame.getDescPlanoConvenio());
			novoAltaPedidoExame.setDescProfissional(altaPedidoExame.getDescProfissional());
			novoAltaPedidoExame.setDescUnidade(altaPedidoExame.getDescUnidade());
			novoAltaPedidoExame.setDthrConsulta(altaPedidoExame.getDthrConsulta());
			novoAltaPedidoExame.setFatConvenioSaudePlano(altaPedidoExame.getFatConvenioSaudePlano());
			novoAltaPedidoExame.setIndAgenda(altaPedidoExame.getIndAgenda());
			novoAltaPedidoExame.setTipoPedido(altaPedidoExame.getTipoPedido());
			this.getManterAltaPedidoExameRN().inserirAltaPedidoExame(novoAltaPedidoExame);
			
		}
		
	}
	
	public String gravarAltaPedidoExame(MpmAltaPedidoExame altaPedidoExame) throws ApplicationBusinessException{
		return this.getManterAltaPedidoExameRN().gravarAltaPedidoExame(altaPedidoExame);
	}
	
	public void excluirAltaPedidoExame(MpmAltaPedidoExame altaPedidoExame) throws ApplicationBusinessException {
		this.getManterAltaPedidoExameRN().excluirAltaPedidoExame(altaPedidoExame);
	}

	public MpmAltaPedidoExame obterMpmAltaPedidoExame(Integer altanAtdSeq,
			Integer altanApaSeq, Short altanAsuSeqp) throws ApplicationBusinessException {
		return this.getMpmAltaPedidoExameDAO().obterMpmAltaPedidoExame(altanAtdSeq, altanApaSeq, altanAsuSeqp);
	}
	
	/**
	 * Remove alta pedido exame do sumário ativo
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 */
	public void removerAltaPedidoExame(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		
		MpmAltaPedidoExame altaPedidoExame = this.getMpmAltaPedidoExameDAO().obterMpmAltaPedidoExame(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp());
		
		if (altaPedidoExame != null) {
			
			this.getManterAltaPedidoExameRN().excluirAltaPedidoExame(altaPedidoExame);
			
		}
		
	}
	
	protected MpmAltaPedidoExameDAO getMpmAltaPedidoExameDAO() {
		return mpmAltaPedidoExameDAO;
	}
	
	protected ManterAltaPedidoExameRN getManterAltaPedidoExameRN() {
		return manterAltaPedidoExameRN;
	}
}
