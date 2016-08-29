package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.MpmAltaOutraEquipeSumr;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaOutraEquipeSumrDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterAltaOutraEquipeSumrON extends BaseBusiness {


@EJB
private ManterAltaOutraEquipeSumrRN manterAltaOutraEquipeSumrRN;

private static final Log LOG = LogFactory.getLog(ManterAltaOutraEquipeSumrON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAghuFacade aghuFacade;

@Inject
private MpmAltaOutraEquipeSumrDAO mpmAltaOutraEquipeSumrDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 812049249364902089L;

	public enum ManterAltaOutraEquipeSumrONExceptionCode implements BusinessExceptionCode {
		ATENDIMENTO_NAO_ENCONTRADO;
	}
	
	/**
	 * Retorna a descrição das outras equipes de MPM_ALTA_OUTRA_EQUIPE_SUMRS
	 * @param asuApaAtdSeq
	 * @param asuApaSeq
	 * @param asuSeqp
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String obterDescricaoServicoOutrasEquipes(Integer asuApaAtdSeq, Integer asuApaSeq, Short asuSeqp) throws ApplicationBusinessException {
	
		StringBuffer descricao = null;
		List<MpmAltaOutraEquipeSumr> outrasEquipes = this.getMpmAltaOutraEquipeSumrDAO().obterAltaOutraEquipeSumrs(asuApaAtdSeq, asuApaSeq, asuSeqp);
		
		if (outrasEquipes != null && outrasEquipes.size() > 0) {
		
			for (MpmAltaOutraEquipeSumr altaOutraEquipeSumr : outrasEquipes) {
			
				if (descricao != null) {
						descricao.append('\n').append(CoreUtil.capitalizaTextoFormatoAghu(altaOutraEquipeSumr.getDescServico()));
			
				} else {
						descricao =  new StringBuffer(CoreUtil.capitalizaTextoFormatoAghu(altaOutraEquipeSumr.getDescServico()));		
				}
			
			}
	
		}
		
		return descricao != null ? descricao.toString() : null;
	}
	
	/**
	 * Gera novo MPM_ALTA_OUTRA_EQUIPE_SUMRS para alta sumários
	 * @param altaSumario
	 * @throws ApplicationBusinessException
	 */
	public void gerarAltaOutraEquipeSumr(MpmAltaSumario altaSumario) throws BaseException {
		
		Integer altanAtdSeq = altaSumario.getId().getApaAtdSeq();
		Integer altanApaSeq = altaSumario.getId().getApaSeq();
		AghAtendimentos atendimento = this.getAghuFacade().obterAtendimentoPeloSeq(altanAtdSeq);
		
		if (atendimento != null) {
			
			Integer atdSeq = atendimento.getSeq();
			Short espSeq = atendimento.getEspecialidade().getSeq();	
			
			if (atendimento.getInternacao() != null) {
				
				Integer intSeq = atendimento.getInternacao().getSeq();
				List<AghEspecialidades> listaEspecialides = this.getAghuFacade().pesquisarEspecialidadesPorInternacao(atdSeq, espSeq, intSeq);
				
				for (AghEspecialidades especialidade : listaEspecialides) {
					
					if (!this.getMpmAltaOutraEquipeSumrDAO().possuiAltaOutraEquipeSumr(altanAtdSeq, altanApaSeq, especialidade.getSeq())) {
						
						MpmAltaOutraEquipeSumr altaOutraEquipeSumr = new MpmAltaOutraEquipeSumr();
						
						if (especialidade.getIndImpSoServico().equals(DominioSimNao.N)) {
							
							altaOutraEquipeSumr.setDescServico(CoreUtil.capitalizaTextoFormatoAghu(especialidade.getCentroCusto().getDescricao()));
							
						} else {
							
							String descEspecialidade = CoreUtil.capitalizaTextoFormatoAghu(especialidade.getNomeEspecialidade());
							String descCentroCusto = CoreUtil.capitalizaTextoFormatoAghu(especialidade.getCentroCusto().getDescricao());
							altaOutraEquipeSumr.setDescServico(descCentroCusto + " - " + descEspecialidade);
							
						}
						
						altaOutraEquipeSumr.setMpmAltaSumarios(altaSumario);
						altaOutraEquipeSumr.setEspecialidades(especialidade);
						this.getMpmAltaOutraEquipeSumrDAO().desatachar(altaOutraEquipeSumr);
						this.getManterAltaOutraEquipeSumrRN().inserirAltaOutraEquipeSumr(altaOutraEquipeSumr);
						this.getMpmAltaOutraEquipeSumrDAO().flush();
					}
					
				}
				
			} else if (atendimento.getAtendimentoUrgencia() != null) {
				
				Integer atuSeq = atendimento.getAtendimentoUrgencia().getSeq();
				List<AghEspecialidades> listaEspecialides = this.getAghuFacade().pesquisarEspecialidadesPorAtendUrgencia(atdSeq, espSeq, atuSeq);

				for (AghEspecialidades especialidade : listaEspecialides) {
					
					if (!this.getMpmAltaOutraEquipeSumrDAO().possuiAltaOutraEquipeSumr(altanAtdSeq, altanApaSeq, especialidade.getSeq())) {
						
						MpmAltaOutraEquipeSumr altaOutraEquipeSumr = new MpmAltaOutraEquipeSumr();
						
						if (especialidade.getIndImpSoServico().equals(DominioSimNao.N)) {
							
							altaOutraEquipeSumr.setDescServico(CoreUtil.capitalizaTextoFormatoAghu(especialidade.getCentroCusto().getDescricao()));
							
						} else {
							
							String descEspecialidade = CoreUtil.capitalizaTextoFormatoAghu(especialidade.getNomeEspecialidade());
							String descCentroCusto = CoreUtil.capitalizaTextoFormatoAghu(especialidade.getCentroCusto().getDescricao());
							altaOutraEquipeSumr.setDescServico(descCentroCusto + " - " + descEspecialidade);
							
						}
						
						altaOutraEquipeSumr.setMpmAltaSumarios(altaSumario);
						altaOutraEquipeSumr.setEspecialidades(especialidade);
						this.getMpmAltaOutraEquipeSumrDAO().desatachar(altaOutraEquipeSumr);
						this.getManterAltaOutraEquipeSumrRN().inserirAltaOutraEquipeSumr(altaOutraEquipeSumr);
						this.getMpmAltaOutraEquipeSumrDAO().flush();
						
					}
					
				}	
				
			}
			
		} else {
			
			throw new ApplicationBusinessException (
					ManterAltaOutraEquipeSumrONExceptionCode.ATENDIMENTO_NAO_ENCONTRADO);
			
		}
		
	}
	
	/**
	 * Cria uma cópia de MpmAltaOutraEquipeSumr
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 * @throws ApplicationBusinessException
	 */
	public void versionarAltaOutraEquipeSumr(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws ApplicationBusinessException {
		
		Integer altanAtdSeq = altaSumario.getId().getApaAtdSeq();
		Integer altanApaSeq = altaSumario.getId().getApaSeq();
		
		List<MpmAltaOutraEquipeSumr> outrasEquipes = this.getMpmAltaOutraEquipeSumrDAO().obterAltaOutraEquipeSumrs(altanAtdSeq, altanApaSeq, antigoAsuSeqp);
		
		for (MpmAltaOutraEquipeSumr altaOutraEquipeSumr : outrasEquipes) {
			
			MpmAltaOutraEquipeSumr novoAltaOutraEquipeSumr = new MpmAltaOutraEquipeSumr();
			novoAltaOutraEquipeSumr.setMpmAltaSumarios(altaSumario);
			novoAltaOutraEquipeSumr.setDescServico(CoreUtil.capitalizaTextoFormatoAghu(altaOutraEquipeSumr.getDescServico()));
			novoAltaOutraEquipeSumr.setEspecialidades(altaOutraEquipeSumr.getEspecialidades());
			this.getManterAltaOutraEquipeSumrRN().inserirAltaOutraEquipeSumr(novoAltaOutraEquipeSumr);
			this.getMpmAltaOutraEquipeSumrDAO().flush();
			
		}
		
	}
	
	/**
	 * Remove MpmAltaOutraEquipeSumr
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaOutraEquipeSumr(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		
		List<MpmAltaOutraEquipeSumr> outrasEquipes = this.getMpmAltaOutraEquipeSumrDAO().obterAltaOutraEquipeSumrs(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp());
		
		for (MpmAltaOutraEquipeSumr altaOutraEquipeSumr : outrasEquipes) {
			
			this.getManterAltaOutraEquipeSumrRN().removerAltaOutraEquipeSumr(altaOutraEquipeSumr);
			
		}
		
	}
	
	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected MpmAltaOutraEquipeSumrDAO getMpmAltaOutraEquipeSumrDAO() {
		return mpmAltaOutraEquipeSumrDAO;
	}
	
	protected ManterAltaOutraEquipeSumrRN getManterAltaOutraEquipeSumrRN() {
		return manterAltaOutraEquipeSumrRN;
	}

}
