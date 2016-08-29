package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioIndOrigemItemContaHospitalar;
import br.gov.mec.aghu.dominio.DominioItemConsultoriaSumarios;
import br.gov.mec.aghu.dominio.DominioLocalCobranca;
import br.gov.mec.aghu.dominio.DominioSituacaoItenConta;
import br.gov.mec.aghu.dominio.DominioTipoOperacaoConversao;
import br.gov.mec.aghu.farmacia.dispensacao.business.IFarmaciaDispensacaoFacade;
import br.gov.mec.aghu.farmacia.vo.AfaDispensacaoMdtoVO;
import br.gov.mec.aghu.faturamento.dao.FatItemContaHospitalarDAO;
import br.gov.mec.aghu.model.FatItemContaHospitalar;
import br.gov.mec.aghu.model.FatItemContaHospitalarId;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * ORADB FATK_INTERFACE_AFA
 *
 */

@Stateless
public class FaturamentoFatkInterfaceAfaRN extends BaseBusiness implements Serializable {


@EJB
private ItemContaHospitalarON itemContaHospitalarON;

private static final Log LOG = LogFactory.getLog(FaturamentoFatkInterfaceAfaRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IFaturamentoFacade faturamentoFacade;

@EJB
private IFarmaciaDispensacaoFacade farmaciaDispensacaoFacade;

@Inject
private FatItemContaHospitalarDAO fatItemContaHospitalarDAO;

@EJB
private IServidorLogadoFacade servidorLogadoFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4932113915420277851L;

	/**
	 * ORADB FATK_INTERFACE_AFA.RN_FATP_INS_DISP
	 * @param atdSeq
	 * @param dataInt
	 * @param dataAlta
	 * @param cthSeq
	 * @param opcao
	 * 
	 * Inserir medicamentos na conta hospitalar.
	 * @throws BaseException 
	 */
	public void rnFatpInsDisp(Integer atdSeq, Date dataInt, Date dataAlta, Integer cthSeq, Integer opcao, final Date dataFimVinculoServidor,
			final Boolean pPrevia) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		final Integer P_OPCAO_1 = 1; //EXCLUI PHIS EXISTENTES NA CONTA
		 					//2 =      NÃO EXCLUI PARA OS CASOS DE REINTERNACAO
		
		FatItemContaHospitalarDAO itemContaHospitalarDAO = this.getFatItemContaHospitalarDAO();
		ItemContaHospitalarON itemContaHospitalarON = getItemContaHospitalarON();
		
		
		if(P_OPCAO_1.equals(opcao)){
			List<FatItemContaHospitalar> listaIchAfa = itemContaHospitalarDAO.listarItensContaHospitalarComOrigemAfaEPorContaHospitalarSeq(cthSeq);
			for (FatItemContaHospitalar ich : listaIchAfa) {
				itemContaHospitalarON.removerContaHospitalar(ich, pPrevia);
			}
			/*List<FatItemContaHospitalar> listaIchDig = itemContaHospitalarDAO.listarItensContaHospitalarComOrigemDigFiltrandoPorContaHospitalarEProcedHospInt(cthSeq);
			for (FatItemContaHospitalar ich : listaIchDig) {
				 itemContaHospitalarON.removerContaHospitalar(ich);
			}*/
		}
		List <AfaDispensacaoMdtoVO> lista = getFarmaciaDispensacaoFacade().obterDispensacaoMdtosPorAtendimentoEDataCriacaoEntreDataIntEDataAlta(atdSeq, dataInt, dataAlta);

		Double quantidade = null;
		for (AfaDispensacaoMdtoVO vo : lista) {
			
			//TODO REVISAR A LÓGICA DESTE IF... 
			//Nao foi utilizado um dominio para representar o 'M' pois no momento
			//desta implementacao este atributo ja havia sido utilizado como string em outras estorias ja homologadas.
			if(DominioTipoOperacaoConversao.M.equals(vo.getTipooperconversao())){
				//ceil(reg_mdtos.qtd * nvl(reg_mdtos.fator_conversao,1))
				quantidade = vo.getQuantidade().doubleValue() * (vo.getFatorconversao() != null ? vo.getFatorconversao().doubleValue() : 1 ); 
			}else{
				//ceil(reg_mdtos.qtd / nvl(reg_mdtos.fator_conversao,1))
				quantidade = vo.getQuantidade().doubleValue() / (vo.getFatorconversao() != null ? vo.getFatorconversao().doubleValue() : 1 ); 
			}
			
			quantidade = Math.ceil(quantidade);
			
			Short seq = itemContaHospitalarDAO.obterProximoSeq(cthSeq);
			
			FatItemContaHospitalar itemContaHospitalar = new FatItemContaHospitalar();
			FatItemContaHospitalarId id = new FatItemContaHospitalarId();
			id.setCthSeq(cthSeq);
			id.setSeq(seq);
			itemContaHospitalar.setId(id);
			itemContaHospitalar.setContaHospitalar(getFaturamentoFacade().obterContaHospitalar(cthSeq));
			
			itemContaHospitalar.setIchType(DominioItemConsultoriaSumarios.ICH);
			
			FatProcedHospInternos phi = getFaturamentoFacade().obterProcedimentoHospitalarInterno(vo.getPhiseq());
			itemContaHospitalar.setProcedimentoHospitalarInterno(phi);
			
			itemContaHospitalar.setValor(BigDecimal.ZERO);
			itemContaHospitalar.setIndSituacao(DominioSituacaoItenConta.A);
			
			itemContaHospitalar.setQuantidadeRealizada(quantidade.shortValue());
			
			itemContaHospitalar.setIndOrigem(DominioIndOrigemItemContaHospitalar.AFA);
			itemContaHospitalar.setLocalCobranca(DominioLocalCobranca.I);
			//itemContaHospitalar.setDthrRealizado(vo.getCriadoem());
			itemContaHospitalar.setDthrRealizado(dataAlta);
			itemContaHospitalar.setUnidadesFuncional(null);
			
			itemContaHospitalarON.inserirItemContaHospitalarSemValidacoesForms(itemContaHospitalar, true, servidorLogado, dataFimVinculoServidor, pPrevia);
			//getFaturamentoFacade().evict(itemContaHospitalar);
			
		}
		
	}
	
	private IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}	
	
	protected FatItemContaHospitalarDAO getFatItemContaHospitalarDAO() {
		return fatItemContaHospitalarDAO;
	}
	
	protected ItemContaHospitalarON getItemContaHospitalarON() {
		return itemContaHospitalarON;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected IFarmaciaDispensacaoFacade getFarmaciaDispensacaoFacade() {
		return farmaciaDispensacaoFacade;
	}
}
