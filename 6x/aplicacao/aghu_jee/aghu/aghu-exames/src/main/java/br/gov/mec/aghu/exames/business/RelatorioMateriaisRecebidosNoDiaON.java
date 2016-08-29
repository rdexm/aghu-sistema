package br.gov.mec.aghu.exames.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.exames.dao.VAelExameMatAnaliseDAO;
import br.gov.mec.aghu.exames.vo.RelatorioMateriaisRecebidosNoDiaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

@Stateless
public class RelatorioMateriaisRecebidosNoDiaON extends BaseBusiness {


@Inject
private VAelExameMatAnaliseDAO vAelExameMatAnaliseDAO;

private static final Log LOG = LogFactory.getLog(RelatorioMateriaisRecebidosNoDiaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


	private static final long serialVersionUID = 537907276682381280L;
	
	public enum RelatorioMateriaisRecebidosNoDiaONExceptionCode implements BusinessExceptionCode {
		AEL_01904, MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA, MENSAGEM_INTERVALO_MAIOR_QUE_PERMITIDO;
	}
	
	public List<RelatorioMateriaisRecebidosNoDiaVO> pesquisarMateriaisRecebidosNoDia(Short unfSeq, Date dtInicial, Date dtFinal) throws ApplicationBusinessException{
		
		// Verifica se a data final é menor que a data inicial
		if (DateValidator.validaDataMenor(dtFinal, dtInicial)) {
			throw new ApplicationBusinessException(RelatorioMateriaisRecebidosNoDiaONExceptionCode.AEL_01904);
		}
		
		// Calcula a diferença em dias entre as datas Inicio e Fim
		Integer qtdDiasLimite = 30;
		if (DateUtil.diffInDaysInteger(dtFinal, dtInicial) > qtdDiasLimite) {
			throw new ApplicationBusinessException(RelatorioMateriaisRecebidosNoDiaONExceptionCode.MENSAGEM_INTERVALO_MAIOR_QUE_PERMITIDO);
		}
		
       	String codigoAelSitItemSolic = DominioSituacaoItemSolicitacaoExame.AE.toString();
		
		List<RelatorioMateriaisRecebidosNoDiaVO> vos = getVAelExameMatAnaliseDAO().pesquisarRelatorioMateriaisRecebidosNoDia(dtInicial, dtFinal, unfSeq, codigoAelSitItemSolic);
		
		if(vos == null || vos.isEmpty()){
			throw new ApplicationBusinessException(RelatorioMateriaisRecebidosNoDiaONExceptionCode.MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA);
		}
		
		Set<RelatorioMateriaisRecebidosNoDiaVO> setVO = new HashSet<RelatorioMateriaisRecebidosNoDiaVO>(vos);
		
		for(RelatorioMateriaisRecebidosNoDiaVO vo : vos) {
			this.processaMateriaisRecebidosVO(vo);
		}
		
		List<RelatorioMateriaisRecebidosNoDiaVO> listaOrdenada = new ArrayList<RelatorioMateriaisRecebidosNoDiaVO>(setVO);
		
		CoreUtil.ordenarLista(listaOrdenada, RelatorioMateriaisRecebidosNoDiaVO.Fields.PAC_NOME.toString(), true);
		CoreUtil.ordenarLista(listaOrdenada, RelatorioMateriaisRecebidosNoDiaVO.Fields.ATD_PRONTUARIO.toString(), false);
		CoreUtil.ordenarLista(listaOrdenada, RelatorioMateriaisRecebidosNoDiaVO.Fields.SOE_SEQ.toString(), true);
		CoreUtil.ordenarLista(listaOrdenada, RelatorioMateriaisRecebidosNoDiaVO.Fields.DTHR_EVENTO_STRING_FORMAT.toString(), true);

		//Linhas que possuem o mesmo prontuario,dtHrEvento e pacNome, devem ter algumas informaões da linha "omitidas" na linha subsequente
		/*for(int i =0 ; i< listaOrdenada.size(); i++){
			RelatorioMateriaisRecebidosNoDiaVO vo = listaOrdenada.get(i);
			RelatorioMateriaisRecebidosNoDiaVO vo2 = i+1 < listaOrdenada.size() ? listaOrdenada.get(i+1) : null;
			if(vo2 != null && vo2.getSoeSeq().equals(vo.getSoeSeq())
					&& vo2.getDthrEventoFormat().equals(vo.getDthrEventoFormat()) 
					&& (vo2.getProntuario() != null && !CoreUtil.modificados(vo2.getProntuario() , vo.getProntuario())) //pacientes externos não tem prontuario
					&& vo2.getProntuario().equals(vo.getProntuario()) 
					&& vo2.getPacNome().equals(vo.getPacNome()) ){
				vo2.setProntuarioFormat("");
				vo2.setPacNome("");
				vo2.setSoeSeq(null);
			}
		}*/
		
		return listaOrdenada;
	}
	
	private void processaMateriaisRecebidosVO(
			RelatorioMateriaisRecebidosNoDiaVO vo) {
			vo.setSoeSeq(vo.getSoeSeq());
			vo.setProntuarioFormat(vo.getProntuario() == null ? "         " : CoreUtil.formataProntuario(vo.getProntuario()));
			vo.setPacNome(vo.getPacNome());
			vo.setDthrEventoStringFormat(DateUtil.dataToString(vo.getDthrEvento(), "yyyyMMddHHmm"));
			vo.setExameMaterial(obterExameMaterialExtenso(vo.getIndExigeDescricao(), vo.getNomeUsual(), vo.getDescricaoExame(),
					vo.getMaterialAnalise(), vo.getNumeroAp()));
			vo.setOrigemFormat(obterOrigemAtendimento(vo.getOrigem()));
			//Melhoria em Produção #37558
			if(!DominioOrigemAtendimento.I.toString().equals(vo.getOrigem())){
				vo.setLeito("");
			}
	}

	protected String obterExameMaterialExtenso(String indExigeDescricao, String nomeUsual, String descricaoExame, String materialAnalise, 
			Long numeroAp) {
		StringBuffer exameMaterialExtenso = new StringBuffer();
		if ("N".equals(indExigeDescricao)){
			exameMaterialExtenso.append(nomeUsual);
		}else{
			//vem.descricao_exame||' / '||ise.desc_material_analise||decode(ise.numero_ap,null,' ','- AP '||
			//SUBSTR(LPAD(numero_ap,8,'0'),1,6)||'/'||SUBSTR(LPAD(numero_ap,8,'0'),7,2)))
			exameMaterialExtenso.append(descricaoExame.trim()).append(" / ").append(materialAnalise);
			if (numeroAp != null){
				StringBuffer numeroApFormatado = new StringBuffer();
				numeroApFormatado.append(StringUtils.leftPad(numeroAp.toString(),8,"0").substring(0, 6))
				.append('/')
				.append(StringUtils.leftPad(numeroAp.toString(),8,"0").substring(6));
				exameMaterialExtenso.append("-AP ").append(numeroApFormatado);
			}				
		}
		return exameMaterialExtenso.toString();
	}

	private String obterOrigemAtendimento(String origem) {
		if("A".equals(origem)){
			return "Ambulatório";
		}else if("I".equals(origem)){
			return "Internação";
		}else if("U".equals(origem)){
			return "Urgência";
		}else if("X".equals(origem)){
			return "Paciente externo";
		}else if("D".equals(origem)){
			return "Doação de Sangue";	
		}else if("H".equals(origem)){
			return "Hospital Dia";
		}else {
			return "Diverso";
		}
	}

	protected VAelExameMatAnaliseDAO getVAelExameMatAnaliseDAO() {
		return vAelExameMatAnaliseDAO;
	}	
	
}