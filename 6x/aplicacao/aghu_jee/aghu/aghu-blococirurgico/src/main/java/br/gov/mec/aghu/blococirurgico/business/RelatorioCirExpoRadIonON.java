package br.gov.mec.aghu.blococirurgico.business;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiasExposicaoRadiacaoIonizanteVO;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateFormatUtil;

@Stateless
public class RelatorioCirExpoRadIonON extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(RelatorioCirExpoRadIonON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;


	private static final long serialVersionUID = -9169172235740919213L;
	private static final String QUEBRA_LINHA = "\r\n";
	private static final String SEPARADOR=";";
	private static final String ENCODE="ISO-8859-1";
	private static final String EXTENSAO=".csv";
	
	public enum RelatorioCirExpoRadIonONExceptionCode implements BusinessExceptionCode {
		REL_CERI_MENSAGEM_VALIDACAO_PROFISSIONAL, REL_CERI_PARAMETROS_OBRIGATORIOS;
	}
	
	public void validarProfissional(final RapPessoasFisicas rapPessoasFisicas)throws ApplicationBusinessException {
		if(rapPessoasFisicas !=  null){
			throw new ApplicationBusinessException(RelatorioCirExpoRadIonONExceptionCode.REL_CERI_MENSAGEM_VALIDACAO_PROFISSIONAL);
		}
	}
	
	private String gerarCabecalhoDoRelatorio() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getResourceBundleValue("HEADER_CSV_REL_CERI_DATA"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_CSV_REL_CERI_HORA_INICIO_CIRG"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_CSV_REL_CERI_SALA"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_CSV_REL_CERI_PRONTUARIO"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_CSV_REL_CERI_ESP"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_CSV_REL_CERI_ESPECIALIDADE"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_CSV_REL_CERI_ATUACAO"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_CSV_REL_CERI_MATRICULA"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_CSV_REL_CERI_VINCULO"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_CSV_REL_CERI_NOME_DO_PROFISSIONAL"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_CSV_REL_CERI_PROCEDIMENTO"))
			.append(SEPARADOR)
			.append(getResourceBundleValue("HEADER_CSV_REL_CERI_EQUIPAMENTO"))
			.append(QUEBRA_LINHA);

		return buffer.toString();
	}
	
	private String gerarLinhasDoRelatorio(CirurgiasExposicaoRadiacaoIonizanteVO linha) {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append(linha.getStrDataDiaMesAno())
			.append(SEPARADOR)
			.append(linha.getStrDataInicioCirurgia())
			.append(SEPARADOR)
			.append(linha.getSciNome())
			.append(SEPARADOR)
			.append(linha.getStrProntuario())
			.append(SEPARADOR)
			.append(linha.getSigla())
			.append(SEPARADOR)
			.append(linha.getNomeEspecialidade())
			.append(SEPARADOR)
			.append(linha.getAtuacao())
			.append(SEPARADOR)
			.append(linha.getMatricula())
			.append(SEPARADOR)
			.append(linha.getVinCodigo())
			.append(SEPARADOR)
			.append(linha.getNomeProf())
			.append(SEPARADOR)
			.append(linha.getProcedimento())
			.append(SEPARADOR)
			.append(linha.getStrEquipamento())
			.append(QUEBRA_LINHA);
		
		return buffer.toString();
	}
	
	private MbcCirurgiasDAO getMbcCirurgiasDAO(){
		return mbcCirurgiasDAO;
	}
	
	private String deparaEquipamento(Short equipamento){
		switch (equipamento) {
		case 7:
			return getResourceBundleValue("REL_CERI_INTENSIFICADOR_IMAGEM");
		case 105:
			return getResourceBundleValue("REL_CERI_GAMA_PROBI");
		case 111:
			return getResourceBundleValue("REL_CERI_INTENSIFICADOR_IMAGEM_FLUOROSCOPIA");
		case 109:
			return getResourceBundleValue("REL_CERI_FLUOROSCOPIA");
		default:
			return "";
		}
	}
	
	@SuppressWarnings("unchecked")
	private void ordernarLista(List<CirurgiasExposicaoRadiacaoIonizanteVO> lista) {
		final ComparatorChain comparatorChain = new ComparatorChain();
		
		final BeanComparator dataComparator = new BeanComparator(
				CirurgiasExposicaoRadiacaoIonizanteVO.Fields.STR_DATA_DIA_MES_ANO.toString(), new NullComparator(false));
		
		final BeanComparator salaComparator = new BeanComparator(
				CirurgiasExposicaoRadiacaoIonizanteVO.Fields.SCI_NOME.toString(), new NullComparator(false));
		
		comparatorChain.addComparator(dataComparator);
		comparatorChain.addComparator(salaComparator);
		
		Collections.sort(lista, comparatorChain);
		
	}

	/**
	 * Procedure 
	 * 
	 * ORADB: GERA_ARQUIVO_UTIL_EQ (form MBCF_GERA_ARQUIVOS) 
	 * 
	 * @param dataInicial
	 * @param dataFinal
	 * @param unidadesFuncionais
	 * @param equipamentos
	 * @return
	 */
	private List <CirurgiasExposicaoRadiacaoIonizanteVO> obterCirurgiasExposicaoRadiacaoIonizante(
			Date dataInicial, Date dataFinal, List<Short> unidadesFuncionais,
			List<Short> equipamentos){
		
		List<CirurgiasExposicaoRadiacaoIonizanteVO> uniqueCollection = new ArrayList<CirurgiasExposicaoRadiacaoIonizanteVO>();
		uniqueCollection.addAll(getMbcCirurgiasDAO().criteriaUnion1CirurgiasExposicaoRadiacaoIonizante(dataInicial, dataFinal, unidadesFuncionais, equipamentos));
		uniqueCollection.addAll(getMbcCirurgiasDAO().criteriaUnion2CirurgiasExposicaoRadiacaoIonizante(dataInicial, dataFinal, unidadesFuncionais, equipamentos));
		uniqueCollection.addAll(getMbcCirurgiasDAO().criteriaUnion3CirurgiasExposicaoRadiacaoIonizante(dataInicial, dataFinal, unidadesFuncionais, equipamentos));	
		
		for (CirurgiasExposicaoRadiacaoIonizanteVO vo : uniqueCollection){
			vo.setStrDataDiaMesAno(vo.getData());
			vo.setStrDataInicioCirurgia(DateFormatUtil.formataHoraMinuto(vo.getDataInicioCirurgia()));
			vo.setSciNome(StringUtils.substring(vo.getSciNome(), 0, 60));
			vo.setStrProntuario(String.valueOf(vo.getProntuario()));
			vo.setSigla(StringUtils.substring(vo.getSigla(), 0, 3));
			vo.setNomeEspecialidade(StringUtils.substring(vo.getNomeEspecialidade(), 0, 50));
			
			if(vo.getTipoAtuacao() != null){
				vo.setAtuacao(StringUtils.substring(vo.getTipoAtuacao().toString(), 0, 3));
			}else{
				vo.setAtuacao(StringUtils.substring(vo.getFuncaoProfissional().toString(), 0, 3));
			}
			
			vo.setMatricula(Integer.parseInt(StringUtils.substring(String.valueOf(vo.getMatricula()), 0, 7)));
			vo.setVinCodigo(Short.parseShort(StringUtils.substring(String.valueOf(vo.getVinCodigo()), 0, 3)));

			if (vo.getNomeProf() != null) {
				vo.setNomeProf(StringUtils.substring(vo.getNomeProf(), 0, 50));
			} else { // Função RAPC_BUSCA_NOME pode retornar nulo
				vo.setNomeProf("");
			}
			
			vo.setProcedimento(StringUtils.substring(vo.getProcedimento(), 0, 30));
			vo.setStrEquipamento(StringUtils.substring(deparaEquipamento(vo.getEquipamento()), 0, 30));
		}
		
		Set<CirurgiasExposicaoRadiacaoIonizanteVO> setIonizanteVOs =  new HashSet<CirurgiasExposicaoRadiacaoIonizanteVO>();
		setIonizanteVOs.addAll(uniqueCollection);
		
		List<CirurgiasExposicaoRadiacaoIonizanteVO> list = new ArrayList<CirurgiasExposicaoRadiacaoIonizanteVO>(setIonizanteVOs);
		ordernarLista(list);
		
		return list;
	}
	
	public String geraRelCSVCirurgiasExposicaoRadiacaoIonizante(
			Date dataInicial, Date dataFinal, List<Short> unidadesFuncionais,
			List<Short> equipamentos) throws IOException, ApplicationBusinessException {
		
		if (dataInicial == null || dataFinal == null) {
			throw new ApplicationBusinessException(RelatorioCirExpoRadIonONExceptionCode.REL_CERI_PARAMETROS_OBRIGATORIOS);
		}
		
		final List<CirurgiasExposicaoRadiacaoIonizanteVO> listaLinhas = obterCirurgiasExposicaoRadiacaoIonizante(dataInicial, dataFinal, unidadesFuncionais, equipamentos);

		final File file = File.createTempFile(DominioNomeRelatorio.RELAT_CIRURGIAS_EXPOSICAO_RADIACAO_IONIZANTE.toString(), EXTENSAO);

		final Writer out = new OutputStreamWriter(new FileOutputStream(file), ENCODE);

		out.write(gerarCabecalhoDoRelatorio());

		if (!listaLinhas.isEmpty()) {
			for(CirurgiasExposicaoRadiacaoIonizanteVO linha : listaLinhas){
				out.write(gerarLinhasDoRelatorio(linha));
			}
		}
		
		out.flush();
		out.close();

		return file.getAbsolutePath();
	}

}