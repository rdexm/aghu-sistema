package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NotasPolVO;
import br.gov.mec.aghu.util.CapitalizeEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ConsultaNotasPolON extends BaseBusiness {


@EJB
private ConsultaNotasPolRN consultaNotasPolRN;

private static final Log LOG = LogFactory.getLog(ConsultaNotasPolON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAmbulatorioFacade ambulatorioFacade;

	private static final long serialVersionUID = -5368913992653808232L;

	public enum ConsultaNotasPolONExceptionCode implements	BusinessExceptionCode {
		MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA
	}
	

	public List<NotasPolVO> pesquisarNotasPol(Integer seqNota) throws BaseException {
		NotasPolVO notaVO = new NotasPolVO();
		List<NotasPolVO> notasVO = new ArrayList<NotasPolVO>();
		
		MamNotaAdicionalEvolucoes nota = getAmbulatorioFacade().buscarNotaParaRelatorio(seqNota);
		String[] dadosPaciente = getConsultarNotasPolRN().buscarDadosPaciente(nota);
		
		notaVO.setCabecalho(getConsultarNotasPolRN().buscarDadosCabecalho(nota));
		notaVO.setDescricao(nota.getDescricao());
		notaVO.setAssinado(getConsultarNotasPolRN().buscarDadosRodape(nota));
		notaVO.setNomePaciente(getAmbulatorioFacade().obterDescricaoCidCapitalizada(dadosPaciente[0], CapitalizeEnum.TODAS));
		notaVO.setProntuarioPaciente(CoreUtil.formataProntuario(dadosPaciente[1]));
		notaVO.setSeqNotaAdicional(nota.getSeq());
		notaVO.setDataHora(new Date());
		
		notasVO.add(notaVO);
		
		if(notasVO.isEmpty()){
			throw new ApplicationBusinessException(ConsultaNotasPolONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA);
		}else{
			MamNotaAdicionalEvolucoes notaNew = getAmbulatorioFacade().obterNotaAdicionalEvolucoesPorChavePrimaria(nota.getSeq()); 
			notaNew.setImpresso(true);
			MamNotaAdicionalEvolucoes notaOld = getAmbulatorioFacade().obterNotaAdicionalEvolucoesOriginal(nota.getSeq());
			
			executaPosImpressao(notaNew, notaOld);
		}
		return notasVO;
	}
	
	public Integer recuperarVersaoDoc(Integer seqNota){
		return getConsultarNotasPolRN().buscarVersaoSeqDoc(seqNota, DominioTipoDocumento.NPO);
	}

	public Boolean visualizarRelatorio(Integer seqNota) {
		if(getConsultarNotasPolRN().verificarCertificadoAssinado(seqNota, DominioTipoDocumento.NPO)){
			return getConsultarNotasPolRN().chamarDocCertificado(seqNota, DominioTipoDocumento.NPO);
		}else{
			return false;
		}
	}
	
	protected ConsultaNotasPolRN getConsultarNotasPolRN() {
		return consultaNotasPolRN;
	}

	protected void executaPosImpressao(MamNotaAdicionalEvolucoes notaAdicionalEvolucoes, MamNotaAdicionalEvolucoes notaAdicionalEvolucoesOld) throws BaseException {
		getAmbulatorioFacade().atualizarNotaAdicionalEvolucoes(notaAdicionalEvolucoes, notaAdicionalEvolucoesOld);
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return this.ambulatorioFacade;
	}
}
