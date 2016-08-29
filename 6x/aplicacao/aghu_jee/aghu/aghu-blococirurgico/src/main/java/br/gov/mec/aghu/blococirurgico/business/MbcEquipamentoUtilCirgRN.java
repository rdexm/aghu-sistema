package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoCirurgicoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcEquipamentoUtilCirgDAO;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcEquipamentoCirurgico;
import br.gov.mec.aghu.model.MbcEquipamentoUtilCirg;
import br.gov.mec.aghu.model.MbcEquipamentoUtilCirgId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class MbcEquipamentoUtilCirgRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcEquipamentoUtilCirgRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcEquipamentoUtilCirgDAO mbcEquipamentoUtilCirgDAO;

	@Inject
	private MbcEquipamentoCirurgicoDAO mbcEquipamentoCirurgicoDAO;

	
	private static final long serialVersionUID = -443432099947287734L;

	public void persistirListaMbcEquipamentoUtilCirg(List<MbcEquipamentoUtilCirg> equipamentos, 
			MbcCirurgias cirurgia) throws ApplicationBusinessException{
		
		for(MbcEquipamentoUtilCirg registro : equipamentos){
			if(registro.getQuantidade() != null && registro.getQuantidade() > 0){
				if(registro.getId() == null){
					MbcEquipamentoUtilCirgId id = new MbcEquipamentoUtilCirgId(cirurgia.getSeq(), registro.getMbcEquipamentoCirurgico().getSeq());
					registro.setId(id);
					registro.setMbcCirurgias(cirurgia);
					this.antesInserir(registro);				
					this.getMbcEquipamentoUtilCirgDAO().persistir(registro);
					this.depoisInserir(registro);
				} else {
					MbcEquipamentoUtilCirg velho = this.getMbcEquipamentoUtilCirgDAO().obterOriginal(registro);
					this.antesAtualizar(registro, velho);
					this.getMbcEquipamentoUtilCirgDAO().atualizar(registro);
					this.depoisAtualizar(registro, velho);
				}
			} else {
				if(registro.getId() != null){
					this.excluirMbcEquipamentoUtilCirg(registro);
				}
			}
		}
	}
	
	public void excluirMbcEquipamentoUtilCirg(MbcEquipamentoUtilCirg equipamento) throws ApplicationBusinessException{
		this.antesRemover(equipamento);
		this.getMbcEquipamentoUtilCirgDAO().remover(equipamento);
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: MBCT_EQC_BRI
	 * 
	 * @param MbcEquipamentoUtilCirg
	 * @param RapServidores
	 */
	private void antesInserir(MbcEquipamentoUtilCirg equipamento) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		equipamento.setCriadoEm(new Date());
		equipamento.setRapServidores(servidorLogado);
		if(equipamento.getQuantidade() != null && equipamento.getQuantidade() > 0){
			equipamento.setIndUso(Boolean.TRUE);
		}
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: MBCT_EQC_ARI
	 * 
	 * @param MbcEquipamentoUtilCirg
	 * @throws ApplicationBusinessException 
	 */
	private void depoisInserir(MbcEquipamentoUtilCirg equipamento) throws ApplicationBusinessException{
		if(equipamento.getQuantidade() != null && equipamento.getQuantidade() > 0){
			this.getMbcEquipamentoUtilCirgDAO().executarFfcInterfaceEqp(equipamento, MbcEquipamentoUtilCirgDAO.TipoAcao.I);
		}
	}
	
	private MbcEquipamentoUtilCirgDAO getMbcEquipamentoUtilCirgDAO(){
		return mbcEquipamentoUtilCirgDAO;
	}
	
	private MbcEquipamentoCirurgicoDAO getMbcEquipamentoCirurgicoDAO(){
		return mbcEquipamentoCirurgicoDAO;
	}
	
	private MbcCirurgiasDAO getMbcCirurgiasDAO(){
		return mbcCirurgiasDAO;
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: MBCT_EQC_BRU
	 * 
	 * @param MbcEquipamentoUtilCirg
	 * @param RapServidores
	 */
	private void antesAtualizar(MbcEquipamentoUtilCirg novoEquipamento, MbcEquipamentoUtilCirg velhoEquipamento) throws ApplicationBusinessException {
		if(novoEquipamento != null && novoEquipamento.getQuantidade() > 0 
				|| velhoEquipamento != null && velhoEquipamento.getQuantidade() > 0){
			novoEquipamento.setIndUso(Boolean.TRUE);
		}
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: MBCT_EQC_ARU
	 * 
	 * @param MbcEquipamentoUtilCirg
	 * @param RapServidores
	 * @throws ApplicationBusinessException 
	 */
	private void depoisAtualizar(MbcEquipamentoUtilCirg novoEquipamento, MbcEquipamentoUtilCirg velhoEquipamento) throws ApplicationBusinessException{
		if(CoreUtil.modificados(novoEquipamento.getQuantidade(), velhoEquipamento.getQuantidade())){
			if(((Short)CoreUtil.nvl(novoEquipamento.getQuantidade(), 0)).intValue() == 0){
				this.getMbcEquipamentoUtilCirgDAO().executarFfcInterfaceEqp(novoEquipamento, MbcEquipamentoUtilCirgDAO.TipoAcao.I);
			} else {
				this.getMbcEquipamentoUtilCirgDAO().executarFfcInterfaceEqp(novoEquipamento, MbcEquipamentoUtilCirgDAO.TipoAcao.A);
			}
		} else if(novoEquipamento.getQuantidade() <= 0){
			this.getMbcEquipamentoUtilCirgDAO().executarFfcInterfaceEqp(novoEquipamento, MbcEquipamentoUtilCirgDAO.TipoAcao.E);
		}
		
		if(CoreUtil.modificados(novoEquipamento.getIndUso(), velhoEquipamento.getIndUso())){
			if(!novoEquipamento.getIndUso()){
				this.getMbcEquipamentoUtilCirgDAO().executarFfcInterfaceEqp(novoEquipamento, MbcEquipamentoUtilCirgDAO.TipoAcao.E);
			} else if(novoEquipamento.getIndUso() && 
					((Short)CoreUtil.nvl(novoEquipamento.getQuantidade(), 0)).intValue() > 0){
				this.getMbcEquipamentoUtilCirgDAO().executarFfcInterfaceEqp(novoEquipamento, MbcEquipamentoUtilCirgDAO.TipoAcao.I);
			}
		}
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: MBCT_EQC_BRD
	 * 
	 * @param MbcEquipamentoUtilCirg
	 * @param RapServidores
	 * @throws ApplicationBusinessException 
	 */
	private void antesRemover(MbcEquipamentoUtilCirg velhoEquipamento) throws ApplicationBusinessException{
		this.getMbcEquipamentoUtilCirgDAO().executarFfcInterfaceEqp(velhoEquipamento, MbcEquipamentoUtilCirgDAO.TipoAcao.I);
	}
	
	/**
	 * 
	 * PROCEDURE: WHEN-NEW-BLOCK-INSTANCE
	 * 
	 * @param  Integer
	 * @throws ApplicationBusinessException 
	 */
	public List<MbcEquipamentoUtilCirg> pesquisarRegraWhenNewBlockInstance(Integer crgSeq) throws ApplicationBusinessException{
		MbcCirurgias cirurgia = this.getMbcCirurgiasDAO().obterOriginal(crgSeq);
		
		List<MbcEquipamentoUtilCirg> listaMbcEquipamentoUtilCirg = this.getMbcEquipamentoUtilCirgDAO().pesquisarMbcEquipamentoUtilCirgPorCirurgia(crgSeq, null);
		List<MbcEquipamentoCirurgico> listaMbcEquipamentoCirurgico = null;
		
		//CASO NAO TENHA ENCOTRADO NA listaMbcEquipamentoUtilCirg
		if(listaMbcEquipamentoUtilCirg == null 
				|| listaMbcEquipamentoUtilCirg.size() == 0){
			
			listaMbcEquipamentoCirurgico = this.getMbcEquipamentoCirurgicoDAO()
				.pesquisarMbcEquipamentoCirurgicoComSubQueryMbcUnidadenotaSala(cirurgia, 
						DominioIndRespProc.AGND, 
						DominioSituacao.A, "S");
			
			//CASO NAO TENHA ENCOTRADO listaMbcEquipamentoCirurgico
			if(listaMbcEquipamentoCirurgico == null 
					|| listaMbcEquipamentoCirurgico.size() == 0){
				
				listaMbcEquipamentoCirurgico = this.getMbcEquipamentoCirurgicoDAO()
				.pesquisarMbcEquipamentoCirurgicoComSubQueryMbcUnidadenotaSala(cirurgia,
						DominioSituacao.A);
			}
			
			//CASO !!AINDA!! NAO TENHA ENCOTRADO listaMbcEquipamentoCirurgico
			if(listaMbcEquipamentoCirurgico == null 
					|| listaMbcEquipamentoCirurgico.size() == 0){
				
				listaMbcEquipamentoCirurgico = this.getMbcEquipamentoCirurgicoDAO()
				.pesquisarMbcEquipamentoCirurgicoComSubQueryMbcUnidadenotaSala(cirurgia);
			}
			
			listaMbcEquipamentoUtilCirg = new ArrayList<MbcEquipamentoUtilCirg>();
			
			for(MbcEquipamentoCirurgico mbcEquipamentoCirurgico : listaMbcEquipamentoCirurgico){
				MbcEquipamentoUtilCirg novo = new MbcEquipamentoUtilCirg();
				novo.setMbcEquipamentoCirurgico(mbcEquipamentoCirurgico);
				listaMbcEquipamentoUtilCirg.add(novo);
			}
		}
		
		return listaMbcEquipamentoUtilCirg;
	}

	public void excluirListaMbcEquipamentoUtilCirgPorMbcCirurgiaEEquipamentoCirurgico(Integer crgSeq, Short euuSeq) throws ApplicationBusinessException {
		List<MbcEquipamentoUtilCirg> listaEquipUtilCir = this.getMbcEquipamentoUtilCirgDAO().pesquisarMbcEquipamentoUtilCirgPorCirurgia(crgSeq, euuSeq);
		for(MbcEquipamentoUtilCirg equipUtilCir : listaEquipUtilCir){
			this.antesRemover(equipUtilCir);
			this.getMbcEquipamentoUtilCirgDAO().remover(equipUtilCir);
		}
	}
}
