package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.AghParemetrosONExceptionCode;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDestinoPacienteDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDestinoPaciente;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class RelatorioPacientesEmSalaRecuperacaoPorUnidadeON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RelatorioPacientesEmSalaRecuperacaoPorUnidadeON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;

	@Inject
	private MbcDestinoPacienteDAO mbcDestinoPacienteDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;


	@EJB
	private EscalaCirurgiasON escalaCirurgiasON;

	@EJB
	private IAmbulatorioFacade iAmbulatorioFacade;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private IPesquisaInternacaoFacade iPesquisaInternacaoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -302289684487580176L;

	public List<RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO> listarPacientesEmSalaRecuperacaoPorUnidade(Short seqUnidadeCirurgica, String ordemListagem) throws ApplicationBusinessException {
		
		MbcDestinoPaciente destPaciente = recuperaDestinoPaciente();
		
		Byte pDestSalaRec = destPaciente.getSeq();
		
		AghParametros diasRetroativos = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_RETROATIVOS_REL_PAC_SALA_RECUP);
		
		List<MbcCirurgias> listPacSalaRecuperacao = getMbcCirurgiasDAO().listarPacientesSalaRecuperacaoPorUnidade(seqUnidadeCirurgica, pDestSalaRec, diasRetroativos.getVlrNumerico());
		
		List<RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO> listVo = new ArrayList<RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO>();
		for (MbcCirurgias cirurgia : listPacSalaRecuperacao) {
			RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO vo = new RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO();
			vo.setUnidade(cirurgia.getUnidadeFuncional().getDescricao()); 
			vo.setDataHora(new Date());
			vo.setQuarto(getEscalaCirurgiasON().pesquisaQuarto(cirurgia.getPaciente().getCodigo()));
			vo.setDthrEntrada(cirurgia.getDataEntradaSr());
			vo.setPacNome(cirurgia.getPaciente().getNome( ));
			vo.setCirurgiao(preencherNomeCirurgiao(cirurgia.getSeq()));
			//#27603
			if(cirurgia.getPaciente()!=null && cirurgia.getPaciente().getProntuario()!=null){
				vo.setProntuario(cirurgia.getPaciente().getProntuario().toString());
			}else{
				vo.setProntuario("        ");
			}
			if(cirurgia.getAtendimento() != null){
				vo.setUnidCti(getPesquisaInternacaoFacade().verificarCaracteristicaDaUnidadeFuncional(cirurgia.getAtendimento().getUnidadeFuncional().getSeq(), ConstanteAghCaractUnidFuncionais.UNID_CTI).toString());
			}else{
				vo.setUnidCti(DominioSimNao.N.toString());
			}
			if("S".equals(vo.getUnidCti())){
				vo.setTituloHeader("INFORMAÇÕES DO PACIENTE EM LEITO DE CTI");
				vo.setNomeFooter("TOTAL DE PACIENTES EM CTI :");
			}else{
				vo.setTituloHeader("INFORMAÇÕES DO PACIENTE EM SRPA");
				vo.setNomeFooter("TOTAL DE PACIENTES EM SR :");
			}
			listVo.add(vo);
		}
		
		if("1".equals(ordemListagem)){			
			CoreUtil.ordenarLista(listVo, RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO.Fields.PAC_NOME.toString(), true);
			CoreUtil.ordenarLista(listVo, RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO.Fields.UNID_CTI.toString(), true);//#27603
		}else{			
			CoreUtil.ordenarLista(listVo, RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO.Fields.PAC_NOME.toString(), true);//27700
			CoreUtil.ordenarLista(listVo, RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO.Fields.CRG_DTHR_ENTRADA_SR.toString(), true);
			CoreUtil.ordenarLista(listVo, RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO.Fields.UNID_CTI.toString(), true);//#27603
		}
		
		return listVo;
	}
	
	private MbcDestinoPaciente recuperaDestinoPaciente() throws ApplicationBusinessException {

		AghParametros parametroDestPaciente = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DEST_SALA_REC);

		if (parametroDestPaciente != null && parametroDestPaciente.getVlrNumerico() == null) {
			throw new ApplicationBusinessException(AghParemetrosONExceptionCode.VALOR_PARAMETRO_NAO_DEFINIDO, AghuParametrosEnum.P_DEST_SALA_REC.toString());
		}

		MbcDestinoPaciente destPaciente = getMbcDestinoPacienteDAO().obterOriginal(parametroDestPaciente.getVlrNumerico().byteValue());

		return destPaciente;
	}

	

	

	
	public String preencherNomeCirurgiao(Integer crgSeq) {
		String retorno = "";
		DominioFuncaoProfissional[] listFuncaoProfissional = new DominioFuncaoProfissional[] { DominioFuncaoProfissional.MCO, DominioFuncaoProfissional.MPF};
		List<MbcProfCirurgias> listProfCirurgias = getMbcProfCirurgiasDAO().listarMbcProfCirurgiasPorCrgSeqFuncaoProfissionalOrder(crgSeq, listFuncaoProfissional,null);
		for (MbcProfCirurgias profCirurgias : listProfCirurgias) {
			if(!retorno.equals("")){
				retorno = retorno.concat("\n");
			}
			if(profCirurgias.getServidorPuc().getPessoaFisica().getNomeUsual() != null){
				retorno = retorno.concat(profCirurgias.getServidorPuc().getPessoaFisica().getNomeUsual());
			}else{
				retorno = retorno.concat(getAmbulatorioFacade().formataString(profCirurgias.getServidorPuc().getPessoaFisica().getNome(), 16));
			}
		}
		return retorno;
	}

	protected EscalaCirurgiasON getEscalaCirurgiasON() {
		return escalaCirurgiasON;
	}
	
	protected MbcCirurgiasDAO getMbcCirurgiasDAO() {
		return mbcCirurgiasDAO;
	}
	
	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}
	
	protected MbcDestinoPacienteDAO getMbcDestinoPacienteDAO(){
		return mbcDestinoPacienteDAO;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return iAmbulatorioFacade;
	}
	
	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade(){
		return iPesquisaInternacaoFacade;
	}
}
