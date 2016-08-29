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
import br.gov.mec.aghu.blococirurgico.vo.RelatorioControleChamadaPacienteVO;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioControleChamadaPacienteON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioControleChamadaPacienteON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;


	@EJB
	private EscalaCirurgiasON escalaCirurgiasON;

	private static final long serialVersionUID = 2602336726448189350L;
	
	public enum RelatorioControleChamadaPacienteONExceptionCode implements	BusinessExceptionCode {
	MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA
}

	
	public List<RelatorioControleChamadaPacienteVO> recuperarRelatorioControleChamadaPacienteVO(Short unfSeq, Date dataCirurgia) throws ApplicationBusinessException  {
		
		List<MbcCirurgias> listaCirurgias = getMbcCirurgiasDAO().pesquisarCirurgiasPorUnidadeDataSolicitacao(unfSeq,null, dataCirurgia, false,false, false, MbcCirurgias.Fields.NOME);
		
		if(listaCirurgias.isEmpty()){
			throw new ApplicationBusinessException(RelatorioControleChamadaPacienteONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA);
		}
		
		//C2
		List<RelatorioControleChamadaPacienteVO> listaRelatorioControleChamadaPacienteVO = new ArrayList<RelatorioControleChamadaPacienteVO>();
		
		for(MbcCirurgias cirurgia : listaCirurgias){
			
			RelatorioControleChamadaPacienteVO vo = new RelatorioControleChamadaPacienteVO();
			
			vo.setHrInicioCirurgia(DateUtil.obterDataFormatada(cirurgia.getDataInicioCirurgia(), "HH:mm"));
			vo.setNome(cirurgia.getPaciente().getNome());
			vo.setSala(cirurgia.getSalaCirurgica().getId().getSeqp());
			vo.setOrigem(cirurgia.getOrigemPacienteCirurgia().toString());
			vo.setQuarto(getEscalaCirurgiasON().pesquisaQuarto(cirurgia.getPaciente().getCodigo()));//MBCC_LOCAL_AIP_PAC
			vo.setNumeroAgenda(cirurgia.getNumeroAgenda());
			vo.setCrgSeq(cirurgia.getSeq());
						
			listaRelatorioControleChamadaPacienteVO.add(vo);
		}		
		
		return listaRelatorioControleChamadaPacienteVO;
	}	
		
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	protected EscalaCirurgiasON getEscalaCirurgiasON() {
		return escalaCirurgiasON;
	}
	
}
