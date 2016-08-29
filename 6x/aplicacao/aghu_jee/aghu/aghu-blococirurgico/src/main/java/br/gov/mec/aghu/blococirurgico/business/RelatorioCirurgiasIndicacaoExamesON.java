package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProcEspPorCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicHemoCirgAgendadaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSolicitacaoEspExecCirgDAO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiasIndicacaoExamesVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcSolicitacaoEspExecCirg;
import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class RelatorioCirurgiasIndicacaoExamesON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioCirurgiasIndicacaoExamesON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProcEspPorCirurgiasDAO mbcProcEspPorCirurgiasDAO;

	@Inject
	private MbcSolicHemoCirgAgendadaDAO mbcSolicHemoCirgAgendadaDAO;

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private MbcSolicitacaoEspExecCirgDAO mbcSolicitacaoEspExecCirgDAO;


	@EJB
	private EscalaCirurgiasON escalaCirurgiasON;

	@EJB
	private RelatorioPacientesComCirurgiaPorUnidadeON relatorioPacientesComCirurgiaPorUnidadeON;

	private static final long serialVersionUID = 2602336726448189350L;
	
	public enum RelatorioCirurgiasIndicacaoExamesONExceptionCode implements	BusinessExceptionCode {
	MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA
}

	
	public List<RelatorioCirurgiasIndicacaoExamesVO> recuperarRelatorioCirurgiasIndicacaoExames(Short unfSeq, Short unidExecExames, Date dataCirurgia,
			Boolean cirgComSolicitacao) throws ApplicationBusinessException {	
	
		List<RelatorioCirurgiasIndicacaoExamesVO> relatorioCirurgiasList =null;
	
		List<MbcCirurgias> listaCirurgias = getMbcCirurgiasDAO().pesquisarCirurgiasPorUnidadeDataSolicitacao(unfSeq,unidExecExames, dataCirurgia, cirgComSolicitacao, true,true, null);
		
		if(listaCirurgias.isEmpty()){
			throw new ApplicationBusinessException(RelatorioCirurgiasIndicacaoExamesONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA);
		}

		relatorioCirurgiasList = new ArrayList<RelatorioCirurgiasIndicacaoExamesVO>();

		for(MbcCirurgias cirurgia : listaCirurgias){
			
			RelatorioCirurgiasIndicacaoExamesVO vo = new RelatorioCirurgiasIndicacaoExamesVO();
			
			vo.setReq(processarCfReqFormula(cirurgia.getSeq(), unidExecExames));//RN1	
			vo.setHrInicioCirurgia(DateUtil.obterDataFormatada(cirurgia.getDataInicioCirurgia(), "HH:mm"));
			vo.setProntuario(CoreUtil.formataProntuario(cirurgia.getPaciente().getProntuario()));
			vo.setNome(cirurgia.getPaciente().getNome());
			vo.setSala(cirurgia.getSalaCirurgica().getId().getSeqp());
			vo.setHrFimCirurgia(DateUtil.obterDataFormatada(cirurgia.getDataFimCirurgia(), "HH:mm"));			
			vo.setQuarto(getEscalaCirurgiasON().pesquisaQuarto(cirurgia.getPaciente().getCodigo()));//MBCC_LOCAL_AIP_PAC
			vo.setConvenio(cirurgia.getConvenioSaudePlano().getConvenioSaude().getDescricao());
			vo.setCirurgiaoList(processarListaCirurgiao(cirurgia.getSeq()));
			vo.setProcedimentosList(processarListaProcedimentos(cirurgia.getSeq()));   
			vo.setExamesList(processarListaExames(cirurgia.getSeq(),unidExecExames));   
			vo.setAgenda(cirurgia.getNumeroAgenda());
			vo.setCrgSeq(cirurgia.getSeq());
						
			relatorioCirurgiasList.add(vo);
		}		
		Collections.sort(relatorioCirurgiasList, Collections.reverseOrder(new Comparator<RelatorioCirurgiasIndicacaoExamesVO>() {
			@Override
			public int compare(RelatorioCirurgiasIndicacaoExamesVO o1, RelatorioCirurgiasIndicacaoExamesVO o2) {
				return o1.getReq().compareTo(o2.getReq());
			}
		}));
		
		return relatorioCirurgiasList;
	}
	
	private List<LinhaReportVO> processarListaExames(Integer crgSeq, Short unidExecExames ) {
		
		List<LinhaReportVO> listaExames= new ArrayList<LinhaReportVO>();
		
		List<MbcSolicitacaoEspExecCirg> listSolicExames = getMbcSolicitacaoEspExecCirgsDAO().pesquisarMaterialSolicitacaoEspecialidadeExecutaEscalaCirurgica(crgSeq, unidExecExames);
		
		for (MbcSolicitacaoEspExecCirg solicitacaoEspExecCirg : listSolicExames) {
			
			LinhaReportVO vo = new LinhaReportVO();			
			vo.setTexto1(solicitacaoEspExecCirg.getMbcNecessidadeCirurgica().getDescricao());
			
			listaExames.add(vo);			
		}	
		if(listaExames.isEmpty()){
			return null;
		}
		return listaExames;		
	}
	
	private List<LinhaReportVO> processarListaProcedimentos(Integer crgSeq) {
		
		List<LinhaReportVO> listaProcedimentos= new ArrayList<LinhaReportVO>();
		
		List<MbcProcEspPorCirurgias> listProcEspPorCirurgias = getMbcProcEspPorCirurgiasDAO().listarMbcProcEspPorCirurgiasPorCrgSeq(crgSeq, true, false, false);
		for (MbcProcEspPorCirurgias procEspPorCirurgias : listProcEspPorCirurgias) {
			
			LinhaReportVO linhaReportProcedimento = new LinhaReportVO();			
			linhaReportProcedimento.setTexto1(procEspPorCirurgias.getProcedimentoCirurgico().getDescricao());
			
			listaProcedimentos.add(linhaReportProcedimento);			
		}	
		if(listaProcedimentos.isEmpty()){
			return null;
		}
		return listaProcedimentos;		
	}
	
	private List<LinhaReportVO> processarListaCirurgiao(Integer crgSeq) {
		
		List<LinhaReportVO> listaCirurgiao= new ArrayList<LinhaReportVO>();
		
		DominioFuncaoProfissional[] listFuncaoProfissional = new DominioFuncaoProfissional[] { DominioFuncaoProfissional.MCO, DominioFuncaoProfissional.MPF};
		List<MbcProfCirurgias> listProfCirurgias = getMbcProfCirurgiasDAO().listarMbcProfCirurgiasPorCrgSeqFuncaoProfissional(crgSeq, listFuncaoProfissional,null);
		for (MbcProfCirurgias profCirurgias : listProfCirurgias) {
			
			LinhaReportVO linhaReportCirurgiao = new LinhaReportVO();
			if(profCirurgias.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNomeUsual() != null){
				linhaReportCirurgiao.setTexto1(profCirurgias.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNomeUsual());
			}else{
				linhaReportCirurgiao.setTexto1(profCirurgias.getMbcProfAtuaUnidCirgs().getRapServidores().getPessoaFisica().getNome());
			}
			listaCirurgiao.add(linhaReportCirurgiao);			
		}
		if(listaCirurgiao.isEmpty()){
			return null;
		}
		return listaCirurgiao;
	}

	private String processarCfReqFormula(Integer crgSeq, Short unidExecExames) {
		
		List<MbcSolicitacaoEspExecCirg> lista = getMbcSolicitacaoEspExecCirgsDAO().pesquisarMaterialSolicitacaoEspecialidadeExecutaEscalaCirurgica(crgSeq, unidExecExames);
		
		if(lista != null && !lista.isEmpty()){
			return "SIM";
		}
		return "NÃO";		
	}
		
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	protected MbcSolicitacaoEspExecCirgDAO getMbcSolicitacaoEspExecCirgsDAO() {
		return mbcSolicitacaoEspExecCirgDAO;
	}
	
	protected MbcProcEspPorCirurgiasDAO getMbcProcEspPorCirurgiasDAO() {
		return mbcProcEspPorCirurgiasDAO;
	}
	
	protected MbcSolicHemoCirgAgendadaDAO getMbcSolicHemoCirgAgendadaDAO() {
		return mbcSolicHemoCirgAgendadaDAO;
	}
	
	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}
	
	protected EscalaCirurgiasON getEscalaCirurgiasON() {
		return escalaCirurgiasON;
	}
	
	protected RelatorioPacientesComCirurgiaPorUnidadeON getRelatorioPacientesComCirurgiaPorUnidadeON() {
		return relatorioPacientesComCirurgiaPorUnidadeON;
	}

}
